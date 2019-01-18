package pl.edu.mimuw.cloudatlas.fetcher;

import pl.edu.mimuw.cloudatlas.agent.agentExceptions.AgentException;
import pl.edu.mimuw.cloudatlas.agent.agentExceptions.ZoneNotFoundException;
import pl.edu.mimuw.cloudatlas.cloudAtlasAPI.CloudAtlasAPI;
import pl.edu.mimuw.cloudatlas.model.*;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.Properties;

public class Fetcher implements Runnable {
    private boolean set = false;

    private final String metricsFile;
    private final String nodePath;
    private final String contacts;
    private final String fallbackContacts;
    private final String agentHost;

    private CloudAtlasAPI cloudAtlas;

    public Fetcher(Properties properties) {
        this.metricsFile = properties.getProperty("metricsFile");
        this.nodePath = properties.getProperty("nodePath");
        this.contacts = properties.getProperty("contacts");
        this.fallbackContacts = properties.getProperty("fallbackContacts");
        this.agentHost = properties.getProperty("agentHost");
    }

    private void bindCloudAtlas() {
        System.out.println("Trying to bind CloudAtlas");
        try {
            Registry registry = LocateRegistry.getRegistry(agentHost);
            cloudAtlas = (CloudAtlasAPI) registry.lookup("CloudAtlasAPI");
            System.out.println("CloudAtlas bound");

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
                cloudAtlas.setFallbackContacts(contactsSet);
            } else {
                ValueSet contactsSet = (ValueSet) ModelReader.formValue("set contact", contacts);
                System.out.println("Setting contacts = " + contactsSet);
                cloudAtlas.setAttribute(nodePath, "contacts", contactsSet);
            }
        } catch (AgentException e) {
            System.out.println("Agent exception while setting " + append + "contacts:");
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        if (!set) {
            try {
                bindCloudAtlas();
                setContacts(true);
                setContacts(false);
                set = true;
            } catch (RemoteException e) {
                System.out.println("RemoteException, will try to rebind CloudAtlas");
                return;
            }
        }


        Runtime rt = Runtime.getRuntime();
        String toExec = "./utility/fetch_metrics " + metricsFile + " " + nodePath;
        try {
            try {
                Process pr = rt.exec(toExec);

                pr.waitFor();
            } catch (IOException e) {
                System.out.println("IOException during execution of " + toExec);
                System.out.println("Check the file utility/fetch_metrics!");
                return;
            }

            for (Map.Entry<String, AttributesMap> zone : ModelReader.readAttributes(metricsFile).entrySet()) {
                for (Map.Entry<Attribute, Value> entry : zone.getValue()) {
                    System.out.println("Setting " + entry.getKey().getName() + " = " + entry.getValue());
                    cloudAtlas.setAttribute(zone.getKey(), entry.getKey().getName(), entry.getValue());
                    try {
                        cloudAtlas.setAttribute(zone.getKey(), entry.getKey().getName(), entry.getValue());
                    } catch (ZoneNotFoundException e) {
                        System.out.println("Zone not found: " + zone.getKey());
                        return;
                    } catch (AgentException e) {
                        System.out.println("Agent exception while adding: " + entry + " to " + zone.getKey());
                        System.out.println(e.getMessage());
                    }
                }
            }
        } catch (AgentException e) {
            System.out.println("Agent exception while setting fallback contacts:");
            System.out.println(e.getMessage());
        } catch (RemoteException e) {
            System.out.println("RemoteException, trying to rebind CloudAtlas");
            set = false;
        } catch (InterruptedException e) {
            System.out.println("Interrupted exception");
        } catch (IOException e) {
            System.out.println("IOException related to reading attributes from: " + metricsFile);
        } catch (Exception other) {
            System.out.println("Unexpected exception occured!");
            other.printStackTrace();
        }
    }
}
