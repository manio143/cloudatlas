package pl.edu.mimuw.cloudatlas.fetcher;

import pl.edu.mimuw.cloudatlas.model.*;
import pl.edu.mimuw.cloudatlas.cloudAtlasAPI.CloudAtlasAPI;

import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Fetcher {
    private String iniFile;
    private String host;

    public Fetcher(String host, String iniFile) {
        this.host = host;
        this.iniFile = iniFile;
    }

    private static ValueContact createContact(String path, byte ip1, byte ip2, byte ip3, byte ip4)
            throws UnknownHostException {
        return new ValueContact(new PathName(path), InetAddress.getByAddress(new byte[] {
                ip1, ip2, ip3, ip4
        }));
    }

    private void run() {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(iniFile));
//            System.out.println(prop.getProperty("collection_interval"));

            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }
            Registry registry = LocateRegistry.getRegistry(host);
            CloudAtlasAPI stub = (CloudAtlasAPI) registry.lookup("CloudAtlasAPI");

            List<ValueContact> contacts = new ArrayList<ValueContact>();
            contacts.add(createContact("/uw/khaki13", (byte)10, (byte)1, (byte)1, (byte)38));
            stub.setFallbackContacts(contacts);

            String pathName = "/uw/violet07";
            List<Pair> attributes = new ArrayList<Pair>();
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
            attributes.add(new Pair("expiry", new ValueDuration("+1 12:00:00.000")));

            for (Pair p : attributes) {
//                System.out.println(p.val);
                stub.setAttribute(pathName, p.attr, p.val);
            }


        } catch (Exception e) {
            System.out.println(e);
        }
    }


    public static void main(String[] args) {
        Fetcher f = new Fetcher(args[0], args[1]);
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
