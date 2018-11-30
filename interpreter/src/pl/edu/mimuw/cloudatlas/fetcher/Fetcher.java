package pl.edu.mimuw.cloudatlas.fetcher;

import pl.edu.mimuw.cloudatlas.model.*;
import pl.edu.mimuw.cloudatlas.cloudAtlasAPI.CloudAtlasAPI;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.ParseException;
import java.util.*;

public class Fetcher {
    private String host;
    private String iniFile;
    private String startFile;
    private String metricsFile;

    public Fetcher(String host, String iniFile, String startFile, String metricsFile) {
        this.host = host;
        this.iniFile = iniFile;
        this.startFile = startFile;
        this.metricsFile = metricsFile;
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

            case "double":
                return new ValueDouble(Double.parseDouble(valueString));
            case "duration":
                return new ValueDuration(valueString);
            case "int":
                return new ValueInt(Long.parseLong(valueString));
            case "string":
                return new ValueString(valueString);
            case "set":
                Set<Value> valuesSet = new HashSet<>();
                for (String elem : valueString.substring(1,valueString.length() - 1).split(",")) {
                    String trimmedElem = elem.trim();
                    System.out.println(trimmedElem.substring(1, trimmedElem.length() - 1));
                    valuesSet.add(createValue(types, trimmedElem.substring(1, trimmedElem.length() - 1), which + 1));
                }
                return new ValueSet(new HashSet<>(valuesSet), typeFromStrings(types, which + 1));
            case "list":
                List<Value> valuesList = new ArrayList<>();
                for (String elem : valueString.substring(1,valueString.length() - 1).split(",")) {
                    String trimmedElem = elem.trim();
                    valuesList.add(createValue(types, trimmedElem.substring(1, trimmedElem.length() - 1), which + 1));
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

    private void run() {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(iniFile));
            System.out.println(prop.getProperty("collection_interval"));

            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec("./fetch_metrics " + startFile + " " + metricsFile);

            pr.waitFor();

            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }
            Registry registry = LocateRegistry.getRegistry(host);
            CloudAtlasAPI stub = (CloudAtlasAPI) registry.lookup("CloudAtlasAPI");

            List<ValueContact> contacts = new ArrayList<ValueContact>();
            contacts.add(createContact("/uw/khaki13", (byte)10, (byte)1, (byte)1, (byte)38));
            stub.setFallbackContacts(contacts);

            String pathName = "";
            List<Pair> attributes = new ArrayList<Pair>();

            try(BufferedReader br = new BufferedReader(new FileReader(metricsFile))) {
                String line = br.readLine();
                pathName = line;

                System.out.println(pathName);

                line = br.readLine();

                while (line != null) {
                    System.out.println(line);
                    String[] parts = line.split(":");
                    String attribute = parts[0].trim();
                    String secondPart = line.substring(parts[0].length() + 1, line.length()).trim();
                    System.out.println(secondPart);
                    String[] typesAndValue = secondPart.split("=");
                    String[] types = typesAndValue[0].trim().split(" ");
                    String valueString = typesAndValue[1].trim();
                    System.out.println(attribute);
                    System.out.println(types[0]);
                    System.out.println(valueString);
                    Value val = createValue(types, valueString, 0);
                    line = br.readLine();
                }
            }

            attributes.add(new Pair("creation", new ValueTime("2011/11/09 20:8:13.123")));
            attributes.add(new Pair("cpu_usage", new ValueDouble(0.9)));
            attributes.add(new Pair("num_cores", new ValueInt(3L)));
            attributes.add(new Pair("num_processes", new ValueInt(131L)));
            attributes.add(new Pair("has_ups", new ValueBoolean(null)));
            List<Value> some_names = new ArrayList<>();
            some_names.add(new ValueString("tola"));
            some_names.add(new ValueString("tosia"));
            ValueList vl = new ValueList(new ArrayList<Value>(some_names), TypePrimitive.STRING);
            attributes.add(new Pair("some_names", vl));
            attributes.add(new Pair("expiry", new ValueDuration(13,12,0,0,0)));

            for (Pair p : attributes) {
                System.out.println(p.val);
                stub.setAttribute(pathName, p.attr, p.val);
            }


        } catch (Exception e) {
            System.out.println(e);
        }
    }


    public static void main(String[] args) {
        Fetcher f = new Fetcher(args[0], args[1], args[2], args[3]);
        f.run();
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
