package pl.edu.mimuw.cloudatlas.client;

import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientServer {

    public static void main(String[] args) {
        try {
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }

            Properties prop = new Properties();
            prop.load(new FileInputStream(args[0]));
            Long interval = Long.parseLong(prop.getProperty("collectionInterval"));
            String host = prop.getProperty("host");
            String signerHost = prop.getProperty("signerHost");
            Integer port = Integer.parseInt(prop.getProperty("port"));
            Integer maxResultsSize = Integer.parseInt(prop.getProperty("maxResultsSize"));

            Client client = new Client(host, signerHost, port, interval, maxResultsSize);
            if (client.isSet()) {
                ScheduledExecutorService scheduler =
                        Executors.newSingleThreadScheduledExecutor();
                scheduler.scheduleAtFixedRate(client, 0L, interval, TimeUnit.SECONDS);
            } else {
                System.out.println("Server not updated!");
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}