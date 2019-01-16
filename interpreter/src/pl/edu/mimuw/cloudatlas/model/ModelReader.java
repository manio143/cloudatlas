package pl.edu.mimuw.cloudatlas.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.*;

public class ModelReader {
    public static ValueContact createContact(String path, byte ip1, byte ip2, byte ip3, byte ip4)
            throws UnknownHostException {
        return new ValueContact(new PathName(path), InetAddress.getByAddress(new byte[] {
                ip1, ip2, ip3, ip4
        }));
    }

    public static Type typeFromStrings(String[] types, int which) {
        switch(types[which]) {
            case "boolean":
                return TypePrimitive.BOOLEAN;
            case "contact":
                return TypePrimitive.CONTACT;
            case "double":
                return TypePrimitive.DOUBLE;
            case "duration":
                return TypePrimitive.DURATION;
            case "integer":
                return TypePrimitive.INTEGER;
            case "string":
                return TypePrimitive.STRING;
            case "set":
                return new TypeCollection(Type.PrimaryType.SET, typeFromStrings(types, which + 1));
            case "list":
                return new TypeCollection(Type.PrimaryType.LIST, typeFromStrings(types, which + 1));
        }
        return TypePrimitive.NULL;
    }

    public static boolean isNull(String valueString) {
        return (valueString.equals("NULL") || valueString.equals("null") || valueString.equals(""));
    }

    public static Value createValue(String[] types, String valueString, int which) {
        switch(types[which]) {
            case "bool":
                if (isNull(valueString)) {
                    return new ValueBoolean(null);
                }
                return new ValueBoolean(Boolean.parseBoolean(valueString));
            case "contact":
                if (isNull(valueString)) {
                    return new ValueContact(null, null);
                }
                String[] parts = valueString.split(";");
                String name = parts[0].trim();
                String address = parts[1].trim();
                try {
                    return new ValueContact(new PathName(name), InetAddress.getByName(address));
                } catch (UnknownHostException e) {
                    System.out.println(e.getMessage());
                }
            case "double":
                if (isNull(valueString)) {
                    return new ValueDouble(null);
                }
                return new ValueDouble(Double.parseDouble(valueString));
            case "duration":
                if (isNull(valueString)) {
                    return new ValueDuration(0L);
                }
                if (valueString.matches("[0-9]+") || valueString.matches("-[0-9]+")) {
                    return new ValueDuration(Long.parseLong(valueString));
                }
                return new ValueDuration(valueString);
            case "integer":
                if (isNull(valueString)) {
                    return new ValueInt(null);
                }
                return new ValueInt(Long.parseLong(valueString));
            case "string":
                if (isNull(valueString)) {
                    return new ValueString(null);
                }
                return new ValueString(valueString.substring(1, valueString.length() - 1));
            case "set":
                if (isNull(valueString)) {
                    return new ValueSet(null);
                }
                Set<Value> valuesSet = new HashSet<>();
                if (valueString.length() > 2) {
                    for (String elem : valueString.substring(1,valueString.length() - 1).split(",")) {
                        valuesSet.add(createValue(types, elem.trim(), which + 1));
                    }
                }
                return new ValueSet(new HashSet<>(valuesSet), typeFromStrings(types, which + 1));
            case "list":
                if (isNull(valueString)) {
                    return new ValueList(null);
                }
                List<Value> valuesList = new ArrayList<>();
                if (valueString.length() > 2) {
                    for (String elem : valueString.substring(1,valueString.length() - 1).split(",")) {
                        valuesList.add(createValue(types, elem.trim(), which + 1));
                    }
                }
                return new ValueList(new ArrayList<>(valuesList), typeFromStrings(types, which + 1));
            case "time":
                if (isNull(valueString)) {
                    return new ValueTime(0L);
                }
                if (valueString.matches("[0-9]+")) {
                    return new ValueTime(Long.parseLong(valueString));
                }
                try {
                    return new ValueTime(valueString);
                } catch (ParseException e) {
                    return new ValueTime(0L);
                }
        }
        return new ValueString("");
    }

    public static Value formValue(String types, String valueString) {
        return createValue(types.trim().split(" "), valueString.trim(), 0);
    }

    public static Pair createAttribute(String line) {
        String[] parts = line.split(":");
        String attribute = parts[0].trim();
        String secondPart = line.substring(parts[0].length() + 1, line.length()).trim();
        String[] typesAndValue = secondPart.split("=");
        return new Pair(attribute, formValue(typesAndValue[0], typesAndValue[1]));
    }

    public static TreeMap<String, AttributesMap> readAttributes(String file) throws IOException {
        TreeMap<String, AttributesMap> resMap = new TreeMap<>();

        AttributesMap map = new AttributesMap();
        String name = "";

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = br.readLine();

        while (line != null) {
            if (!line.trim().equals("")) {
                if (line.charAt(0) == '/') {
                    if (!name.equals("")) {
                        resMap.put(name, map);
                    }
                    map = new AttributesMap();
                    name = line;
                } else {
                    Pair toAdd = createAttribute(line);
                    map.addOrChange(toAdd.attr, toAdd.val);
                }
                line = br.readLine();
            } else {
                break;
            }
        }

        resMap.put(name, map);

        return resMap;
    }

    public static void addSon(ZMI parent, ZMI son, List<String> components, int which) {
        if (which == components.size() - 1) {
            parent.addSon(son);
            son.setFather(parent);
        } else {
            for (ZMI parentSon : parent.getSons()) {
                String name = ((ValueString)parentSon.getAttributes().get("name")).getValue();
                if (name.equals(components.get(which))) {
                    addSon(parentSon, son, components, which + 1);
                    break;
                }
            }
        }
    }

    public static ZMI readZMI(String file) throws IOException {
        ZMI root = new ZMI();

        TreeMap<String, AttributesMap> zoneMaps = ModelReader.readAttributes(file);

        boolean rootSet = false;
        for (Map.Entry<String, AttributesMap> zone : zoneMaps.entrySet()) {
            ZMI son = new ZMI();
            for (Map.Entry<Attribute, Value> entry : zone.getValue()) {
                son.getAttributes().addOrChange(entry.getKey(), entry.getValue());
            }
            PathName pathName = new PathName(zone.getKey());
            if (!rootSet) {
                root = son;
                rootSet = true;
            } else {
                addSon(root, son, pathName.getComponents(), 0);
            }
        }

        return root;
    }

    public static class Pair {
        public String attr;
        public Value val;

        private Pair(String attr, Value val) {
            this.attr = attr;
            this.val = val;
        }
    }
}
