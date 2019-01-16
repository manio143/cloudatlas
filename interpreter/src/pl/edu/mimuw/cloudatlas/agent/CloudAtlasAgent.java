package pl.edu.mimuw.cloudatlas.agent;

import pl.edu.mimuw.cloudatlas.agent.agentExceptions.*;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.GossipInterFreshness;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.GossipSiblings;
import pl.edu.mimuw.cloudatlas.interpreter.Interpreter;
import pl.edu.mimuw.cloudatlas.interpreter.InterpreterException;
import pl.edu.mimuw.cloudatlas.interpreter.QueryResult;
import pl.edu.mimuw.cloudatlas.interpreter.query.Absyn.Program;
import pl.edu.mimuw.cloudatlas.interpreter.query.Yylex;
import pl.edu.mimuw.cloudatlas.interpreter.query.parser;
import pl.edu.mimuw.cloudatlas.model.*;
import pl.edu.mimuw.cloudatlas.signer.SignedQueryRequest;
import pl.edu.mimuw.cloudatlas.cloudAtlasAPI.CloudAtlasAPI;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.PublicKey;
import java.time.Instant;
import java.util.*;

public class CloudAtlasAgent implements CloudAtlasAPI {
    private ZMI root;
    private ValueSet contacts = new ValueSet(new HashSet<>(), TypePrimitive.CONTACT);

    private Map<String, Program> installedQueries = new HashMap<String, Program>();

    private Map<String, List<Attribute>> queryAttributes = new HashMap<String, List<Attribute>>();

    private PublicKey publicKey;

    public CloudAtlasAgent(String zonesFile, PublicKey signerKey) throws IOException {
        publicKey = signerKey;
        root = new ZMI();
        root.getAttributes().addOrChange("name", new ValueString(""));
        root.getAttributes().addOrChange("level", new ValueInt(0L));
    }

    private String getName(ZMI zmi) {
        return ((ValueString) zmi.getAttributes().get("name")).getValue();
    }

    private String getFullName(ZMI zmi) {
        Stack<String> names = new Stack<>();
        while(zmi != null) {
            String name = getName(zmi);
            if(name != null)
                names.push(name);
            zmi = zmi.getFather();
        }
        return new PathName(names).toString();
    }

    public synchronized Map<String, Program> getInstalledQueries() {
        return installedQueries;
    }

    private void removeAttribute(ZMI zmi, Attribute attribute) {
        if (!zmi.getSons().isEmpty()) {
            for (ZMI son : zmi.getSons()) {
                removeAttribute(son, attribute);
            }
            zmi.getAttributes().remove(attribute);
        }
    }

    private void calculateQueries(ZMI zmi, String attributeName) {
        if (!zmi.getSons().isEmpty()) {
            for (ZMI son : zmi.getSons()) {
                calculateQueries(son, attributeName);
            }
            Interpreter interpreter = new Interpreter(zmi);
            Program program = installedQueries.get(attributeName);
            try {
                List<Attribute> columns = queryAttributes.get(attributeName);
                List<QueryResult> result = interpreter.interpretProgram(program);

                for (QueryResult r : result) {
                    zmi.getAttributes().addOrChange(r.getName(), r.getValue());
                    if (zmi.getFather() == null) {
                        columns.add(r.getName());
                    }
                }
                if (result.size() > 0) {
                    updateTimestamp(zmi);
                }
            } catch (InterpreterException exception) {
                queryAttributes.remove(attributeName);
                installedQueries.remove(attributeName);
            }
        }
    }

    private void updateQueries(ZMI zmi, boolean propagateUp) {
        if (!zmi.getSons().isEmpty()) {
            Interpreter interpreter = new Interpreter(zmi);
            for (Map.Entry<String, Program> entry : installedQueries.entrySet()) {
                Program program = entry.getValue();
                try {
                    List<QueryResult> result = interpreter.interpretProgram(program);
                    for (QueryResult r : result) {
                        zmi.getAttributes().addOrChange(r.getName(), r.getValue());
                    }
                    if(result.size() > 0) {
                        updateTimestamp(zmi);
                    }
                } catch (InterpreterException exception) {
                }
            }
        }
        if (propagateUp && zmi.getFather() != null) {
            updateQueries(zmi.getFather(), propagateUp);
        }
    }

    public void recomputeQueries() {
        recomputeQueries(root);
    }

    public void recomputeQueries(ZMI zmi) {
        if (!zmi.getSons().isEmpty()) {
            for (ZMI son : zmi.getSons()) {
                recomputeQueries(son);
            }
            updateQueries(zmi, false);
        }
    }

    private void collectZones(ZMI zmi, List<String> set, String fatherPath) {
        String name = getName(zmi);
        String zone = fatherPath + "/" + name;
        set.add(zone);
        for (ZMI son : zmi.getSons()) {
            collectZones(son, set, zone);
        }
    }

    public List<String> getZones() {
        List<String> res = new ArrayList<String>();
        res.add("/");
        for (ZMI zmi : root.getSons()) {
            collectZones(zmi, res, "");
        }
        return res;
    }

    private ZMI reachZone(String pathName, Integer depthLimit, boolean addNew) {
        PathName pN = new PathName(pathName);
        List<String> comp = pN.getComponents();

        ZMI candidate = root;

        int which = 0;
        boolean found;

        while (which != comp.size() && (depthLimit == null || which != depthLimit)) {
            found = false;
            for (ZMI son : candidate.getSons()) {
                if (getName(son).equals(comp.get(which))) {
                    candidate = son;
                    which++;
                    found = true;
                    break;
                }
            }
            if (!found) {
                if(addNew) {
                    ZMI z = new ZMI(candidate);
                    candidate.addSon(z);
                    z.getAttributes().addOrChange("name", new ValueString(comp.get(which)));
                    z.getAttributes().addOrChange("owner", new ValueString(pathName));
                    z.getAttributes().addOrChange("timestamp", new ValueTime(Instant.now().toEpochMilli()));

                    long parentLevel = ((ValueInt)candidate.getAttributes().get("level")).getValue();
                    z.getAttributes().addOrChange("level", new ValueInt(parentLevel + 1));

                    candidate = z;
                    which++;
                } else {
                    throw new ZoneNotFoundException(pathName);
                }
            }
        }

        return candidate;
    }

    public AttributesMap getAttributes(String pathName) {
        ZMI zmi = reachZone(pathName, null, false);
        return zmi.getAttributes();
    }

    public List<GossipSiblings.Sibling> siblings(int level, String pathName) {
        ZMI zmi = reachZone(pathName, level, false);
        List<GossipSiblings.Sibling> res = new ArrayList<>();
        String fatherName = getFullName(zmi.getFather());
        if(fatherName.equals("/"))
            fatherName = "";
        for (ZMI z : zmi.getFather().getSons()) {
            String name = getName(z);
            String zone = fatherName + "/" + name;
            if (!pathName.startsWith(zone)) {
                ValueSet contacts = (ValueSet) zmi.getAttributes().getOrNull("contacts");
                ValueTime timestamp = (ValueTime) zmi.getAttributes().getOrNull("timestamp");
                List<ValueContact> lvc = new ArrayList<>();
                if(contacts != null)
                    for(Value v : contacts)
                        lvc.add((ValueContact) v);
                res.add(new GossipSiblings.Sibling(new PathName(zone), lvc, timestamp));
            }
        }
        return res;
    }

    public List<GossipInterFreshness.Node> interestingNodes(ValueContact contact) {
        int levels = contact.getName().getComponents().size();
        String pathName = contact.getName().toString();
        List<GossipInterFreshness.Node> nodes = new ArrayList<>();
        for(int l = 1; l <= levels; l++)
            for(GossipSiblings.Sibling sib : siblings(l, pathName))
                nodes.add(new GossipInterFreshness.Node(sib.pathName, sib.timestamp != null ? sib.timestamp : new ValueTime(0L)));
        return nodes;
    }

    public Map<String, List<Attribute>> getQueries() {
        return queryAttributes;
    }

    public void installQueries(SignedQueryRequest sqr) {
        if (!sqr.isValid(publicKey))
            throw new IllegalArgumentException("Invalid request signature");

        String input = sqr.getQuery();
        String[] lines = input.substring(1).split("&");
        for (String line : lines) {
            String[] parts = line.split(":");
            String attributeName = parts[0];
            if (queryAttributes.containsKey(attributeName)) {
                throw new AgentDuplicateQuery(attributeName);
            } else {
                queryAttributes.put(attributeName, new ArrayList<>());
                String[] queries = parts[1].split(";");
                try {
                    for (String query : queries) {
                        Yylex lex = new Yylex(new ByteArrayInputStream(query.getBytes()));
                        try {
                            Program program = (new parser(lex)).pProgram();
                            installedQueries.put(attributeName, program);
                            calculateQueries(root, attributeName);

                        } catch (Exception e) {
                            throw new AgentParserException(query);
                        }
                    }
                } catch (Exception e) {
                    queryAttributes.remove(attributeName);
                    throw e;
                }
            }
        }
    }

    public void uninstallQuery(SignedQueryRequest sqr) {
        if (!sqr.isValid(publicKey))
            throw new IllegalArgumentException("Invalid request signature");

        String queryName = sqr.getQuery();
        if (!installedQueries.containsKey(queryName)) {
            throw new QueryNotFoundException(queryName);
        }
        installedQueries.remove(queryName);
        List<Attribute> attributes = queryAttributes.get(queryName);
        for (Attribute attribute : attributes) {
            removeAttribute(root, attribute);
        }
        queryAttributes.remove(queryName);
    }

    public void setAttribute(String pathName, String attr, Value val) {
        ZMI zmi = reachZone(pathName, null, false);
        if (!zmi.getSons().isEmpty()) {
            throw new NotSingletonZoneException(pathName);
        }
        zmi.getAttributes().addOrChange(attr, val);
        updateTimestamp(zmi);
//        updateQueries(zmi, true);
    }

    public void setAttributes(String pathName, AttributesMap attrs) {
        ZMI zmi = reachZone(pathName, null, true);
        if (!zmi.getSons().isEmpty()) {
            throw new NotSingletonZoneException(pathName);
        }
        zmi.getAttributes().addOrChange(attrs);
        zmi.printAttributes(System.out);
        updateTimestamp(zmi);
//        updateQueries(zmi, true);
    }


    public void setFallbackContacts(ValueSet contacts) {
        this.contacts = contacts;
    }

    public ValueSet getFallbackContacts() {
        return contacts;
    }
    
    private void updateTimestamp(ZMI zmi) {
        zmi.getAttributes().addOrChange("timestamp", new ValueTime(Instant.now().toEpochMilli()));
    }
    public synchronized void safeInstallQuery(String attributeName, Program program) {
        installedQueries.put(attributeName, program);
        queryAttributes.put(attributeName, new ArrayList<>());
    }
}
