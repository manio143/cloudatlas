package pl.edu.mimuw.cloudatlas.agent;

import pl.edu.mimuw.cloudatlas.agent.agentExceptions.*;
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
import java.util.*;

public class CloudAtlasAgent implements CloudAtlasAPI {
    private ZMI root;
    private ValueSet contacts = new ValueSet(new HashSet<>(), TypePrimitive.CONTACT);

    private Map<String, Program> installedQueries = new HashMap<String, Program>();

    private Map<String, List<Attribute>> queryAttributes = new HashMap<String, List<Attribute>>();

    private PublicKey publicKey;

    public CloudAtlasAgent(String zonesFile, PublicKey signerKey) throws IOException {
        publicKey = signerKey;
        try {
            root = ModelReader.readZMI(zonesFile);
        } catch (Exception e) {
            System.out.println("Failed to read ZMIs");
            throw e;
        }
    }

    private String getName(ZMI zmi) {
        return ((ValueString)zmi.getAttributes().get("name")).getValue();
    }

    private void removeAttribute(ZMI zmi, Attribute attribute) {
        if(!zmi.getSons().isEmpty()) {
            for (ZMI son : zmi.getSons()) {
                removeAttribute(son, attribute);
            }
            zmi.getAttributes().remove(attribute);
        }
    }

    private void calculateQueries(ZMI zmi, String attributeName) {
        if(!zmi.getSons().isEmpty()) {
            for (ZMI son : zmi.getSons()) {
                calculateQueries(son, attributeName);
            }
            Interpreter interpreter = new Interpreter(zmi);
            Program program = installedQueries.get(attributeName);
            try {
                List<Attribute> columns = queryAttributes.get(attributeName);
                List<QueryResult> result = interpreter.interpretProgram(program);

                for(QueryResult r : result) {
                    zmi.getAttributes().addOrChange(r.getName(), r.getValue());
                    if (zmi.getFather() == null) {
                        columns.add(r.getName());
                    }
                }
            } catch(InterpreterException exception) {
                queryAttributes.remove(attributeName);
                installedQueries.remove(attributeName);
            }
        }
    }

    private void updateQueries(ZMI zmi) {
        if(!zmi.getSons().isEmpty()) {
            Interpreter interpreter = new Interpreter(zmi);
            for (Map.Entry<String, Program> entry : installedQueries.entrySet()) {
                Program program = entry.getValue();
                try {
                    List<QueryResult> result = interpreter.interpretProgram(program);
                    for (QueryResult r : result) {
                        zmi.getAttributes().addOrChange(r.getName(), r.getValue());
                    }
                } catch (InterpreterException exception) {
                }
            }
        }

        if (zmi.getFather() != null) {
            updateQueries(zmi.getFather());
        }
    }

    private void collectZones(ZMI zmi, List<String> set, String fatherPath) {
        String name = getName(zmi);
        String zone = fatherPath + "/" + name;
        set.add(zone);
        for(ZMI son : zmi.getSons()) {
            collectZones(son, set, zone);
        }
    }

    public synchronized List<String> getZones() {
        List<String> res = new ArrayList<String>();
        res.add("/");
        for (ZMI zmi : root.getSons()) {
            collectZones(zmi, res, "");
        }
        return res;
    }

    private ZMI reachZone(String pathName) {
        PathName pN = new PathName(pathName);
        List<String> comp = pN.getComponents();

        ZMI candidate = root;

        int which = 0;
        boolean found;

        while (which != comp.size()) {
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
                throw new ZoneNotFoundException(pathName);
            }
        }

        return candidate;
    }

    public synchronized AttributesMap getAttributes(String pathName) {
        ZMI zmi = reachZone(pathName);
        return zmi.getAttributes();
    }

    public synchronized Map<String, List<Attribute>> getQueries() {
        return queryAttributes;
    }

    public synchronized void installQueries(SignedQueryRequest sqr) {
        if(!sqr.isValid(publicKey))
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

    public synchronized void uninstallQuery(SignedQueryRequest sqr) {
        if(!sqr.isValid(publicKey))
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

    public synchronized void setAttribute(String pathName, String attr, Value val) {
        ZMI zmi = reachZone(pathName);
        if (!zmi.getSons().isEmpty()) {
            throw new NotSingletonZoneException(pathName);
        }
        zmi.getAttributes().addOrChange(attr, val);
        updateQueries(zmi);
    }

    public synchronized void setFallbackContacts(ValueSet contacts) {
        this.contacts = contacts;
    }
}
