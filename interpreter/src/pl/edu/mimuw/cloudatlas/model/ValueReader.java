package pl.edu.mimuw.cloudatlas.model;

import pl.edu.mimuw.cloudatlas.fetcher.Fetcher;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ValueReader {
    public static ValueContact createContact(String path, byte ip1, byte ip2, byte ip3, byte ip4)
            throws UnknownHostException {
        return new ValueContact(new PathName(path), InetAddress.getByAddress(new byte[] {
                ip1, ip2, ip3, ip4
        }));
    }

    public static Type typeFromStrings(String[] types, int which) {
        switch(types[which]) {
            case "bool":
                return TypePrimitive.BOOLEAN;
            case "contact":
                return TypePrimitive.CONTACT;
            case "double":
                return TypePrimitive.DOUBLE;
            case "duration":
                return TypePrimitive.DURATION;
            case "int":
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

    public static Value createValue(String[] types, String valueString, int which) {
        switch(types[which]) {
            case "bool":
                return new ValueBoolean(Boolean.parseBoolean(valueString));
            case "contact":
                String[] parts = valueString.split(";");
                String name = parts[0].trim();
                String[] address = parts[1].trim().split("\\.");
                Byte[] bytes = new Byte[4];
                for (int i = 0; i < 4; i++) {
                    bytes[i] = (byte)Integer.parseInt(address[i]);
                }
                try {
                    return createContact(name, bytes[0], bytes[1], bytes[2], bytes[3]);
                } catch (UnknownHostException e) {
                    System.out.println(e.getMessage());
                }
            case "double":
                return new ValueDouble(Double.parseDouble(valueString));
            case "duration":
                return new ValueDuration(valueString);
            case "int":
                return new ValueInt(Long.parseLong(valueString));
            case "string":
                return new ValueString(valueString.substring(1, valueString.length() - 1));
            case "set":
                Set<Value> valuesSet = new HashSet<>();
                if (valueString.length() > 2) {
                    for (String elem : valueString.substring(1,valueString.length() - 1).split(",")) {
                        valuesSet.add(createValue(types, elem.trim(), which + 1));
                    }
                }
                return new ValueSet(new HashSet<>(valuesSet), typeFromStrings(types, which + 1));
            case "list":
                List<Value> valuesList = new ArrayList<>();
                if (valueString.length() > 2) {
                    for (String elem : valueString.substring(1,valueString.length() - 1).split(",")) {
                        valuesList.add(createValue(types, elem.trim(), which + 1));
                    }
                }
                return new ValueList(new ArrayList<>(valuesList), typeFromStrings(types, which + 1));
            case "time":
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

    public static class Pair {
        public String attr;
        public Value val;

        private Pair(String attr, Value val) {
            this.attr = attr;
            this.val = val;
        }
    }
}