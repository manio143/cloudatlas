package pl.edu.mimuw.cloudatlas.agent;

import pl.edu.mimuw.cloudatlas.interpreter.Interpreter;
import pl.edu.mimuw.cloudatlas.interpreter.InterpreterException;
import pl.edu.mimuw.cloudatlas.interpreter.QueryResult;
import pl.edu.mimuw.cloudatlas.interpreter.query.Absyn.Program;
import pl.edu.mimuw.cloudatlas.interpreter.query.Yylex;
import pl.edu.mimuw.cloudatlas.interpreter.query.parser;
import pl.edu.mimuw.cloudatlas.model.*;
import pl.edu.mimuw.cloudatlas.cloudAtlasAPI.CloudAtlasAPI;

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.*;

public class CloutAtlasAgent implements CloudAtlasAPI {
    private ZMI root = new ZMI();
    private ValueSet contacts = new ValueSet(new HashSet<>(), TypePrimitive.CONTACT);

    private Map<String, Program> installedQueries = new HashMap<String, Program>();

    private Map<String, List<Attribute>> queryAttributes = new HashMap<String, List<Attribute>>();

    public CloutAtlasAgent() {
        root.getAttributes().add("cardinality", new ValueInt(0L));
        root.getAttributes().add("level", new ValueInt(0L));
        root.getAttributes().add("name", new ValueString(null));
    }

    private String getName(ZMI zmi) {
        return ((ValueString)zmi.getAttributes().get("name")).getValue();
    }

    private String getFullName(ZMI zmi) {
        if (zmi == root) {
            return "/";
        }
        String name = ((ValueString)zmi.getAttributes().get("name")).getValue();
        return getFullName(zmi.getFather()) + "/" + name;
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
                    columns.add(r.getName());
                }
            } catch(InterpreterException exception) {}
        }
    }

    private void updateQueries(ZMI zmi) {
        Interpreter interpreter = new Interpreter(zmi);
        for (Map.Entry<String, Program> entry : installedQueries.entrySet()) {
            Program program = entry.getValue();
            try {
                List<QueryResult> result = interpreter.interpretProgram(program);
                for(QueryResult r : result) {
                    zmi.getAttributes().addOrChange(r.getName(), r.getValue());
                }
            } catch(InterpreterException exception) {}
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

    private ZMI createZone(ZMI parent, String pathName, List<String> comp, int which) {
        ZMI son = new ZMI(parent);
        parent.addSon(son);

        which++;

        son.getAttributes().add("name", new ValueString(comp.get(which - 1)));
        son.getAttributes().add("level", new ValueInt((long) which));
        son.getAttributes().add("owner", new ValueString(pathName));
        son.getAttributes().add("contacts", contacts);
        son.getAttributes().add("cardinality", new ValueInt(1L));

        ValueTime timestamp = new ValueTime(new Timestamp(System.currentTimeMillis()).getTime());
        son.getAttributes().add("timestamp", timestamp);

        if (which == comp.size()) {
            return son;
        } else {
            return createZone(son, pathName, comp, which);
        }
    }

    private boolean childZone(ZMI zmi) {
        return (zmi.getSons().isEmpty() && zmi != root);
    }

    private void increaseCardinality(ZMI zmi) {
        long card = ((ValueInt)zmi.getAttributes().get("cardinality")).getValue();
        zmi.getAttributes().addOrChange("cardinality", new ValueInt(card + 1));
        if (zmi.getFather() != null) {
            increaseCardinality(zmi.getFather());
        }
    }

    private ZMI reachZone(String pathName, boolean createIfNotFound) {
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
                if (createIfNotFound && !childZone(candidate)) {
                    increaseCardinality(candidate);
                    return createZone(candidate, pathName, comp, which);
                } else {
                    throw new ZoneNotFoundException(pathName);
                }
            }
        }

        return candidate;
    }

    public synchronized AttributesMap getAttributes(String pathName) {
        ZMI zmi = reachZone(pathName, false);
        return zmi.getAttributes();
    }

    public synchronized void installQueries(String input) {
        String[] lines = input.substring(1).split("&");
        for (String line : lines) {
            String[] parts = line.split(":");
            String attributeName = parts[0];
            queryAttributes.put(attributeName, new ArrayList<>());
            String[] queries = parts[1].split(";");
            for (String query : queries) {
                Yylex lex = new Yylex(new ByteArrayInputStream(query.getBytes()));
                try {
                    Program program = (new parser(lex)).pProgram();
                    installedQueries.put(attributeName, program);
                    calculateQueries(root, attributeName);
                } catch (Exception e) {
                    System.out.println(e.getStackTrace());
                }
            }
        }
    }

    public synchronized void uninstallQuery(String queryName) {
        if (!installedQueries.containsKey(queryName)) {
            throw new AttributeNotFoundException();
        }
        installedQueries.remove(queryName);
        List<Attribute> attributes = queryAttributes.get(queryName);
        for (Attribute attribute : attributes) {
            removeAttribute(root, attribute);
            queryAttributes.remove(attribute);
        }
    }

    public synchronized void setAttribute(String pathName, String attr, Value val) {
        ZMI zmi = reachZone(pathName, true);
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
