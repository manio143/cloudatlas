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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Fetcher implements Runnable {
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

            ValueSet contacts = (ValueSet)ValueReader.formValue("set contact", "{/uw/khaki;10.1.1.38}");
            stub.setFallbackContacts(contacts);

        } catch (Exception e) {
            System.out.println(e);
        }
    }



    private AttributesMap readAttributes() throws IOException {
        AttributesMap map = new AttributesMap();

        BufferedReader br = new BufferedReader(new FileReader(metricsFile));
        String line = br.readLine();
        pathName = line;

        line = br.readLine();

        while (line != null) {
            ValueReader.Pair toAdd = ValueReader.createAttribute(line);
            map.addOrChange(toAdd.attr, toAdd.val);
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
//                System.out.println(p.getKey() + " : " + p.getValue());
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
            ScheduledExecutorService scheduler =
                    Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(fetcher, 0L, interval, TimeUnit.SECONDS);

        } catch(Exception e) {
            System.out.println(e);
        }
    }
}
