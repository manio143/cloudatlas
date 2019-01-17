package pl.edu.mimuw.cloudatlas.fetcher;

import pl.edu.mimuw.cloudatlas.model.*;
import pl.edu.mimuw.cloudatlas.cloudAtlasAPI.CloudAtlasAPI;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Fetcher implements Runnable {
    boolean set = false;
    private String metricsFile;

    private String header;

    private CloudAtlasAPI stub;

    public Fetcher(String host, String metricsFile, Properties properties) {
        this.metricsFile = metricsFile;

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            Registry registry = LocateRegistry.getRegistry(host);
            stub = (CloudAtlasAPI) registry.lookup("CloudAtlasAPI");

            ValueSet contacts = (ValueSet)ModelReader.formValue("set contact", properties.getProperty("contacts"));
            header = properties.getProperty("zone") + "\n" + "contacts : set contact = "+properties.getProperty("contacts") + "\n";
            stub.setFallbackContacts(contacts);
            this.set = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        try {
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec("./utility/fetch_metrics " + metricsFile);

            pr.waitFor();

            String metrics = new String(Files.readAllBytes(Paths.get(metricsFile)));
            Files.write(Paths.get(metricsFile), (header + metrics).getBytes());

            for (Map.Entry<String, AttributesMap> zone : ModelReader.readAttributes(metricsFile).entrySet()) {

                for (Map.Entry<Attribute, Value> entry : zone.getValue()) {
                    stub.setAttribute(zone.getKey(), entry.getKey().getName(), entry.getValue());
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private boolean isSet() {
        return set;
    }

    public static void main(String[] args) {
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream(args[1]));
            Long interval = Long.parseLong(prop.getProperty("fetchingInterval"));
            Fetcher fetcher = new Fetcher(args[0], args[2], prop);
            if (fetcher.isSet()) {
                ScheduledExecutorService scheduler =
                        Executors.newScheduledThreadPool(1);
                scheduler.scheduleAtFixedRate(fetcher, 0L, interval, TimeUnit.SECONDS);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
