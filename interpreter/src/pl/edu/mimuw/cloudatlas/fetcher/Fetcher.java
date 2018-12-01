package pl.edu.mimuw.cloudatlas.fetcher;

import pl.edu.mimuw.cloudatlas.model.*;
import pl.edu.mimuw.cloudatlas.cloudAtlasAPI.CloudAtlasAPI;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.ParseException;
import java.util.*;

public class Fetcher extends TimerTask {
    private String startFile;
    private String metricsFile;
    private String pathName;

    private CloudAtlasAPI stub;

    public Fetcher(String host, String startFile, String metricsFile) {
        this.startFile = startFile;
        this.metricsFile = metricsFile;

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            Registry registry = LocateRegistry.getRegistry(host);
            stub = (CloudAtlasAPI) registry.lookup("CloudAtlasAPI");

            List<ValueContact> contacts = new ArrayList<ValueContact>();
            contacts.add(createContact("/uw/khaki13", (byte) 10, (byte) 1, (byte) 1, (byte) 38));
            stub.setFallbackContacts(contacts);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static ValueContact createContact(String path, byte ip1, byte ip2, byte ip3, byte ip4)
            throws UnknownHostException {
        return new ValueContact(new PathName(path), InetAddress.getByAddress(new byte[] {
                ip1, ip2, ip3, ip4
        }));
    }

    Type typeFromStrings(String[] types, int which) {
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

    Value createValue(String[] types, String valueString, int which) {
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

    private AttributesMap readAttributes() throws IOException {
        AttributesMap map = new AttributesMap();

        BufferedReader br = new BufferedReader(new FileReader(metricsFile));
        String line = br.readLine();
        pathName = line;

        line = br.readLine();

        while (line != null) {
            String[] parts = line.split(":");
            String attribute = parts[0].trim();
            String secondPart = line.substring(parts[0].length() + 1, line.length()).trim();
            String[] typesAndValue = secondPart.split("=");
            String[] types = typesAndValue[0].trim().split(" ");
            String valueString = typesAndValue[1].trim();
            Value val = createValue(types, valueString, 0);
            map.addOrChange(attribute, val);
            line = br.readLine();
        }

        return map;
    }

    @Override
    public void run(){
        try {
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec("./fetch_metrics " + startFile + " " + metricsFile);

            pr.waitFor();

            for (Map.Entry<Attribute, Value> p : readAttributes()) {
                System.out.println(p.getKey() + " : " + p.getValue());
                stub.setAttribute(pathName, p.getKey().getName(), p.getValue());
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }


    public static void main(String[] args) {
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream(args[1]));
            Long interval = Long.parseLong(prop.getProperty("collection_interval"));
            Fetcher fetcher = new Fetcher(args[0], args[2], args[3]);
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(fetcher, interval, 1000);

        } catch(Exception e) {
            System.out.println(e);
        }
    }

    private class Pair {
        private String attr;
        private Value val;

        private Pair(String attr, Value val) {
            this.attr = attr;
            this.val = val;
        }
    }
}
