package pl.edu.mimuw.cloudatlas.fetcher;

import pl.edu.mimuw.cloudatlas.agent.agentExceptions.AgentException;
import pl.edu.mimuw.cloudatlas.agent.agentExceptions.ZoneNotFoundException;
import pl.edu.mimuw.cloudatlas.agent.utility.Logger;
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
    private boolean isSet = false;

    private final String metricsFile;
    private final String nodePath;
    private final String contacts;
    private final String fallbackContacts;
    private final String agentHost;

    private final Logger logger = new Logger("FETCHER");

    private CloudAtlasAPI cloudAtlas;

    public Fetcher(Properties properties) {
        this.metricsFile = properties.getProperty("metricsFile");
        this.nodePath = properties.getProperty("nodePath");
        this.contacts = properties.getProperty("contacts");
        this.fallbackContacts = properties.getProperty("fallbackContacts");
        this.agentHost = properties.getProperty("agentHost");
    }

    private void bindCloudAtlas() {
        logger.log("Trying to bind CloudAtlas");
        try {
            Registry registry = LocateRegistry.getRegistry(agentHost);
            cloudAtlas = (CloudAtlasAPI) registry.lookup("CloudAtlasAPI");
            logger.log("CloudAtlas bound");
            isSet = true;

        } catch (RemoteException e) {
            logger.errLog("Failed to get registry!");
        } catch (NotBoundException e) {
            logger.errLog("Failed to connect to CloudAtlas agent!");
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
                logger.log("Setting fallback contacts = " + contactsSet);
                cloudAtlas.setFallbackContacts(contactsSet);
            } else {
                ValueSet contactsSet = (ValueSet) ModelReader.formValue("set contact", contacts);
                logger.log("Setting contacts = " + contactsSet);
                cloudAtlas.setAttribute(nodePath, "contacts", contactsSet);
            }
        } catch (AgentException e) {
            logger.errLog("Agent exception while setting " + append + "contacts:");
        }
    }

    @Override
    public void run() {
        if (!isSet) {
            bindCloudAtlas();
            if (!isSet) {
                return;
            }

            try {
                setContacts(true);
                setContacts(false);
                isSet = true;
            } catch (RemoteException e) {
                logger.errLog("RemoteException, will try to rebind CloudAtlas");
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
                logger.errLog("IOException during execution of " + toExec);
                logger.errLog("Check the file utility/fetch_metrics!");
                return;
            }

            for (Map.Entry<String, AttributesMap> zone : ModelReader.readAttributes(metricsFile).entrySet()) {
                for (Map.Entry<Attribute, Value> entry : zone.getValue()) {
                    logger.log("Setting " + entry.getKey().getName() + " = " + entry.getValue());
                    cloudAtlas.setAttribute(zone.getKey(), entry.getKey().getName(), entry.getValue());
                    try {
                        cloudAtlas.setAttribute(zone.getKey(), entry.getKey().getName(), entry.getValue());
                    } catch (ZoneNotFoundException e) {
                        logger.errLog("Zone not found: " + zone.getKey());
                        return;
                    } catch (AgentException e) {
                        logger.errLog("Agent exception while adding: " + entry + " to " + zone.getKey());
                        logger.errLog(e.getMessage());
                    }
                }
            }
        } catch (AgentException e) {
            logger.errLog("Agent exception while setting fallback contacts:");
            logger.errLog(e.getMessage());
        } catch (RemoteException e) {
            logger.errLog("RemoteException, trying to rebind CloudAtlas");
            isSet = false;
        } catch (InterruptedException e) {
            logger.errLog("Interrupted exception");
        } catch (IOException e) {
            logger.errLog("IOException related to reading attributes from: " + metricsFile);
        } catch (Exception other) {
            logger.errLog("Unexpected exception occured!");
            other.printStackTrace();
        }
    }
}
