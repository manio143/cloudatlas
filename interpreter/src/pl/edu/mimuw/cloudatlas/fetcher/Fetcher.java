package pl.edu.mimuw.cloudatlas.fetcher;

import pl.edu.mimuw.cloudatlas.agent.agentExceptions.AgentException;
import pl.edu.mimuw.cloudatlas.model.*;
import pl.edu.mimuw.cloudatlas.cloudAtlasAPI.CloudAtlasAPI;

import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Fetcher implements Runnable {
    boolean set = false;

    private final String metricsFile;
    private final String nodePath;
    private final String contacts;
    private final String fallbackContacts;

    private CloudAtlasAPI stub;

    public Fetcher(Properties properties) {
        this.metricsFile = properties.getProperty("metricsFile");
        this.nodePath = properties.getProperty("nodePath");
        this.contacts = properties.getProperty("contacts");
        this.fallbackContacts = properties.getProperty("fallbackContacts");

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        String host = properties.getProperty("host");
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            stub = (CloudAtlasAPI) registry.lookup("CloudAtlasAPI");
            this.set = true;

        } catch (RemoteException e) {
            System.out.println("Failed to get registry!");
        } catch (NotBoundException e) {
            System.out.println("Failed to connect to CloudAtlas agent!");
        }

    }

    private void setContacts(boolean fallback) throws RemoteException {
        String append = "";
        if (fallback) {
            append = "fallback ";
        }
        try {
            if (fallback) {
                ValueSet contactsSet = (ValueSet) ModelReader.formValue("set contact", fallbackContacts);
                System.out.println("Setting fallback contacts = " + contactsSet);
                stub.setFallbackContacts(contactsSet);
            } else {
                ValueSet contactsSet = (ValueSet) ModelReader.formValue("set contact", contacts);
                System.out.println("Setting contacts = " + contactsSet);
                stub.setAttribute(nodePath, "contacts", contactsSet);
            }
        } catch (AgentException e) {
            System.out.println("Agent exception while setting " + append + "contacts:");
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            setContacts(true);
            setContacts(false);

            Runtime rt = Runtime.getRuntime();
            String toExec = "./utility/fetch_metrics " + metricsFile + " " + nodePath;
            try {
                Process pr = rt.exec(toExec);

                pr.waitFor();

                for (Map.Entry<String, AttributesMap> zone : ModelReader.readAttributes(metricsFile).entrySet()) {
                    for (Map.Entry<Attribute, Value> entry : zone.getValue()) {
                        System.out.println("Setting " + entry.getKey().getName() + " = " + entry.getValue());
                        stub.setAttribute(zone.getKey(), entry.getKey().getName(), entry.getValue());
                        try {
                            stub.setAttribute(zone.getKey(), entry.getKey().getName(), entry.getValue());
                        } catch (AgentException e) {
                            System.out.println("Agent exception while adding: " + entry + " to " + zone.getKey());
                            System.out.println(e.getMessage());
                        }
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("Interrupted exception");
            } catch (IOException e) {
                System.out.println("Exception during execution of: " +toExec);
                e.printStackTrace();
            } catch (AgentException e) {
                System.out.println("Agent exception while setting fallback contacts:");
                System.out.println(e.getMessage());
            } catch (Exception other) {
                System.out.println("Unexpected exception occured!");
                other.printStackTrace();
            }
        } catch (RemoteException e) {
            System.out.println("Remote exception");
        }
    }

    private boolean isSet() {
        return set;
    }

    public static void main(String[] args) {
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream(args[0]));
            Long interval = Long.parseLong(prop.getProperty("fetchingInterval"));
            Fetcher fetcher = new Fetcher(prop);
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
