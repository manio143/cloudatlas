package pl.edu.mimuw.cloudatlas.client;

import java.io.*;
import java.net.InetSocketAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.sun.net.httpserver.HttpServer;
import pl.edu.mimuw.cloudatlas.agent.agentExceptions.AgentException;
import pl.edu.mimuw.cloudatlas.client.handlers.*;
import pl.edu.mimuw.cloudatlas.cloudAtlasAPI.CloudAtlasAPI;
import pl.edu.mimuw.cloudatlas.cloudAtlasAPI.SignerAPI;
import pl.edu.mimuw.cloudatlas.model.*;


public class ClientServer implements Runnable {
    private final int MAX_SIZE = 100;

    private String host;
    private Integer port;
    private Long interval;

    private boolean set = false;
    private final ClientStructures structures = new ClientStructures();

    private boolean isSet() {
        return set;
    }

    private void createServer() throws IOException {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", new FileHandler("www/table.html"));
            server.createContext("/queries", new FileHandler("www/queries.html"));
            server.createContext("/set", new FileHandler("www/setAttribute.html"));
            server.createContext("/contacts", new FileHandler("www/fallback.html"));
            server.createContext("/rmi/all", new AllZonesHandler(structures));
            server.createContext("/rmi/overtime", new OvertimeHandler(structures));
            server.createContext("/rmi/zones", new ZonesHandler(structures));
            server.createContext("/rmi/contacts", new ContactsHandler(structures));
            server.createContext("/rmi/set", new SetAttributeHandler(structures));
            server.createContext("/rmi/attributes", new AttributesHandler(structures));
            server.createContext("/rmi/install", new InstallHandler(structures));
            server.createContext("/rmi/uninstall", new UninstallHandler(structures));
            server.createContext("/rmi/queries", new QueriesHandler(structures));
            server.createContext("/rmi/interval", new IntervalHandler(interval));
            server.createContext("/graph.js", new FileHandler("www/graph.js"));
            server.createContext("/bootstrap.min.js", new FileHandler("www/bootstrap.min.js"));
            server.createContext("/bootstrap.min.css", new FileHandler("www/bootstrap.min.css"));
            server.createContext("/jquery-3.3.1.min.js", new FileHandler("www/jquery-3.3.1.min.js"));
            server.createContext("/jquery.treetable.js", new FileHandler("www/jquery.treetable.js"));
            server.createContext("/persist-min.js", new FileHandler("www/persist-min.js"));
            server.createContext("/jquery.treetable-ajax-persist.js",
                    new FileHandler("www/jquery.treetable-ajax-persist.js"));
            server.createContext("/jquery.treetable.css", new FileHandler("www/jquery.treetable.css"));
            server.createContext("/jquery.treetable.theme.default.css",
                    new FileHandler("www/jquery.treetable.theme.default.css"));
            server.setExecutor(null);
            server.start();
            set = true;

        } catch (IOException e) {
            System.out.println("Failed to create client server!");
            throw e;
        }
    }

    public ClientServer(String host, Integer port, Long interval) throws IOException {
        this.host = host;
        this.port = port;
        this.interval = interval;

        try {
            Registry registry = LocateRegistry.getRegistry(host);

            structures.cloudAtlas = (CloudAtlasAPI) registry.lookup("CloudAtlasAPI");
            System.out.println("CloudAtlas bound");

            structures.signer = (SignerAPI) registry.lookup("SignerAPI");
            System.out.println("Signer bound");

        } catch (RemoteException e) {
            System.out.print("Remote exception while locating registry!");
            e.printStackTrace();

        } catch (NotBoundException e) {
            System.out.print("Exception during CloudAtlas and Signer binding!");
        }

        createServer();
    }

    public static void main(String[] args) {
        try {
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }

            Properties prop = new Properties();
            prop.load(new FileInputStream(args[0]));
            Long interval = Long.parseLong(prop.getProperty("collectionInterval"));
            String host = prop.getProperty("host");
            Integer port = Integer.parseInt(prop.getProperty("port"));
            ClientServer server = new ClientServer(host, port, interval);
            if (server.isSet()) {
                ScheduledExecutorService scheduler =
                        Executors.newSingleThreadScheduledExecutor();
                scheduler.scheduleAtFixedRate(server, 0L, interval, TimeUnit.SECONDS);
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            if (structures.results.size() > MAX_SIZE) {
                structures.results.remove(structures.results.firstKey());
            }
            List<String> zones = structures.cloudAtlas.getZones();
            Map<String, AttributesMap> res = new HashMap<>();
            for (String zone : zones) {
                res.put(zone, structures.cloudAtlas.getAttributes(zone));
            }
            structures.results.put(System.currentTimeMillis(), res);

        } catch (AgentException e) {
            System.out.println(e.getMessage());

        } catch (RemoteException e) {
        }
    }
}