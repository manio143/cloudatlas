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
    private final ZMI root = new ZMI();

    private final List<Program> preinstalled = new LinkedList<>();
    private final Map<SignedQueryRequest, Program> installed = new HashMap<>();
    private final Set<Long> uninstalled = new HashSet<>();

    private final Logger logger = new Logger("AGENT");
    private final PublicKey publicKey;

    private ValueSet contacts = new ValueSet(new HashSet<>(), TypePrimitive.CONTACT);

    public CloudAtlasAgent(String pathName, PublicKey signerKey) throws IOException {
        publicKey = signerKey;
        startNode(pathName);
        preinstallPrograms();
    }

    private void startNode(String pathName) {
        root.getAttributes().addOrChange("name", new ValueString(null));
        root.getAttributes().addOrChange("level", new ValueInt(0L));
        root.getAttributes().addOrChange("freshness", new ValueTime(Instant.now().toEpochMilli()));
        reachZone(pathName, null, true);
    }

    private void preinstallPrograms() {
        List<String> selects = new LinkedList<>();
        selects.add("SELECT first(1, name) AS owner ORDER BY timestamp ASC NULLS LAST");
        selects.add("SELECT first(1, timestamp) AS timestamp ORDER BY timestamp ASC NULLS LAST");
        selects.add("SELECT random(7, unfold(contacts)) AS contacts");
        selects.add("SELECT sum(cardinality) AS cardinality");

        for (String select : selects) {
            Yylex lex = new Yylex(new ByteArrayInputStream(select.getBytes()));
            try {
                Program program = (new parser(lex)).pProgram();
                preinstalled.add(program);
                logger.log("Program preinstalled: " + select);
            } catch (Exception e) {
                logger.errLog("Incorrect query to preinstall: " + select);
            }
        }
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

    public Set<SignedQueryRequest> getInstalledQueries() {
        return installed.keySet();
    }

    private void removeAttribute(ZMI zmi, String attribute) {
        if (!zmi.getSons().isEmpty()) {
            for (ZMI son : zmi.getSons()) {
                removeAttribute(son, attribute);
            }
            zmi.getAttributes().remove(attribute);
        }
    }

    private void computePrograms(Collection<Program> programs, ZMI zmi) {
        Interpreter interpreter = new Interpreter(zmi);
        for (Program program : programs) {
            try {
                List<QueryResult> result = interpreter.interpretProgram(program);
                for (QueryResult r : result) {
                    zmi.getAttributes().addOrChange(r.getName(), r.getValue());
                }
                if(result.size() > 0) {
                    updateTimestamp(zmi);
                }
            } catch (InterpreterException exception) {
                logger.errLog(exception.getMessage());
            }
        }
    }

    private void checkUninstalled() {
        List<SignedQueryRequest> toRemove = new LinkedList<>();

        for (SignedQueryRequest sqr : installed.keySet()) {
            if (uninstalled.contains(sqr.queryID)) {
                toRemove.add(sqr);
            }
        }

        for (SignedQueryRequest sqr : toRemove) {
            for (String attribute : sqr.columns) {
                removeAttribute(root, attribute);
            }
            installed.remove(sqr);
        }
    }

    public void recomputeQueries() {
        recomputeQueries(root);
    }

    private void recomputeQueries(ZMI zmi) {
        if (!zmi.getSons().isEmpty()) {
            for (ZMI son : zmi.getSons()) {
                recomputeQueries(son);
            }
            computePrograms(preinstalled, zmi);
            checkUninstalled();
            computePrograms(installed.values(), zmi);
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
                if (addNew) {
                    ZMI z = new ZMI(candidate);
                    candidate.addSon(z);
                    z.getAttributes().addOrChange("name", new ValueString(comp.get(which)));
                    z.getAttributes().addOrChange("level", new ValueInt((long)which + 1));
                    z.getAttributes().addOrChange("freshness", new ValueTime(Instant.now().toEpochMilli()));

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
        if (fatherName.equals("/")) {
            fatherName = "";
        }
        for (ZMI z : zmi.getFather().getSons()) {
            String name = getName(z);
            String zone = fatherName + "/" + name;
            if (!pathName.startsWith(zone)) {
                ValueSet contacts = (ValueSet) zmi.getAttributes().getOrNull("contacts");
                ValueTime timestamp = (ValueTime) zmi.getAttributes().getOrNull("freshness");
                List<ValueContact> lvc = new ArrayList<>();
                if(contacts != null) {
                    for (Value v : contacts) {
                        lvc.add((ValueContact) v);
                    }
                }
                res.add(new GossipSiblings.Sibling(new PathName(zone), lvc, timestamp));
            }
        }
        return res;
    }

    public List<GossipInterFreshness.Node> interestingNodes(ValueContact contact) {
        int levels = contact.getName().getComponents().size();
        String pathName = contact.getName().toString();
        List<GossipInterFreshness.Node> nodes = new ArrayList<>();
        for(int l = 1; l <= levels; l++) {
            try {
                for (GossipSiblings.Sibling sib : siblings(l, pathName)) {
                    nodes.add(new GossipInterFreshness.Node(sib.pathName, sib.timestamp != null ? sib.timestamp : new ValueTime(0L)));
                }
            } catch (ZoneNotFoundException e) {
                ZMI zmi = reachZone(pathName, l - 1, false);
                String fatherName = getFullName(zmi);
                if (fatherName.equals("/")) {
                    fatherName = "";
                }
                for (ZMI son : zmi.getSons()) {
                    String name = getName(son);
                    String zone = fatherName + "/" + name;
                    ValueTime timestamp = (ValueTime) zmi.getAttributes().getOrNull("freshness");
                    nodes.add(new GossipInterFreshness.Node(new PathName(zone), timestamp != null ? timestamp : new ValueTime(0L)));
                }
                break;
            }
        }
        return nodes;
    }

    public Map<String, List<String>> getQueries() {
        Map<String, List<String>> queries = new HashMap<>();
        for (SignedQueryRequest sqr : installed.keySet()) {
            queries.put(sqr.queryName, sqr.columns);
        }
        return queries;
    }

    public void installQueries(SignedQueryRequest sqr) {
        logger.log("Got query to install");

        if (!sqr.isValid(publicKey)) {
            throw new IllegalArgumentException("Invalid install request signature");
        }

        if (uninstalled.contains(sqr.queryID)) {
            logger.log("Received query was already uninstalled!");
        }

        try {
            Yylex lex = new Yylex(new ByteArrayInputStream(sqr.select.getBytes()));
            Program program = (new parser(lex)).pProgram();

            installed.put(sqr, program);

            logger.log("Successful query installation");
        } catch (Exception e) {
            logger.errLog("Exception while trying to install query");
        }
    }

    public void uninstallQuery(SignedQueryRequest sqr) {
        logger.log("Got query to uninstall");

        if (!sqr.isValid(publicKey)) {
            throw new IllegalArgumentException("Invalid uninstall request signature");
        }

        if (uninstalled.contains(sqr.queryID)) {
            logger.log("Received query was already uninstalled!");
        }

        uninstalled.add(sqr.queryID);
    }

    public void setAttribute(String pathName, String attr, Value val) {
        ZMI zmi = reachZone(pathName, null, false);
        if (!zmi.getSons().isEmpty()) {
            throw new NotSingletonZoneException(pathName);
        }
        zmi.getAttributes().addOrChange(attr, val);
        updateTimestamp(zmi);
    }

    public void setAttributes(String pathName, AttributesMap attrs) {
        ZMI zmi = reachZone(pathName, null, true);
        if (!zmi.getSons().isEmpty()) {
            throw new NotSingletonZoneException(pathName);
        }
        zmi.getAttributes().addOrChange(attrs);
        zmi.printAttributes(System.out);
    }

    public void cleanUp(long diff) {
        cleanUp(root, Instant.now().toEpochMilli(), diff);
    }

    private List<ZMI> toRemove = new ArrayList<>();
    private void cleanUp(ZMI zmi, long now, long diff) {
        for(ZMI son : zmi.getSons())
            cleanUp(son, now, diff);
        for(ZMI r : toRemove)
            r.getFather().removeSon(r);
        ValueTime freshness = (ValueTime) zmi.getAttributes().getOrNull("freshness");
        if(freshness != null && now - freshness.getValue() > diff) {
            logger.log("Cleanup: Zone "+getFullName(zmi) + " is old");
            if(zmi.getSons().size() == 0)
                toRemove.add(zmi);
        }
    }

    public void setFallbackContacts(ValueSet contacts) {
        this.contacts = contacts;
    }

    public ValueSet getFallbackContacts() {
        return contacts;
    }
    
    private void updateTimestamp(ZMI zmi) {
        zmi.getAttributes().addOrChange("freshness", new ValueTime(Instant.now().toEpochMilli()));
    }
}
