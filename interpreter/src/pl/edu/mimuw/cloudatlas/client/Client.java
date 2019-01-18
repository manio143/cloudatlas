package pl.edu.mimuw.cloudatlas.client;

import com.sun.net.httpserver.HttpServer;
import pl.edu.mimuw.cloudatlas.agent.CloudAtlasAgent;
import pl.edu.mimuw.cloudatlas.agent.agentExceptions.AgentException;
import pl.edu.mimuw.cloudatlas.client.handlers.*;
import pl.edu.mimuw.cloudatlas.cloudAtlasAPI.CloudAtlasAPI;
import pl.edu.mimuw.cloudatlas.cloudAtlasAPI.SignerAPI;
import pl.edu.mimuw.cloudatlas.model.AttributesMap;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client implements Runnable {
    private int port;
    private long interval;
    private int maxResultsSize;

    private boolean set = false;
    private final ClientStructures structures = new ClientStructures();

    public boolean isSet() {
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

    private static void bindCloudAtlas(Registry registry, ClientStructures structures) {
        try {
            structures.cloudAtlas = (CloudAtlasAPI) registry.lookup("CloudAtlasAPI");
            System.out.println("CloudAtlas bound");

        } catch (NotBoundException e) {
            System.out.print("NotBoundException during CloudAtlas binding!");
        } catch (AccessException e) {
            System.out.print("AccessException during CloudAtlas binding!");
        } catch (RemoteException e) {
            System.out.println("RemoteException during CloudAtlas binding!");
        }
    }

    private static void bindSigner(Registry registry, ClientStructures structures) {
        try {
            structures.signer = (SignerAPI) registry.lookup("SignerAPI");
            System.out.println("Signer bound");
        } catch (NotBoundException e) {
            System.out.print("NotBoundException during Signer binding!");
        } catch (AccessException e) {
            System.out.print("AccessException during Signer binding!");
        } catch (RemoteException e) {
            System.out.println("RemoteException during Signer binding!");
        }
    }

    public static void rebindCloudAtlas(ClientStructures structures) {
        try {
            Registry registry = LocateRegistry.getRegistry(structures.getHost());
            bindCloudAtlas(registry, structures);
            System.out.println("CloudAtlas rebound");
        } catch (RemoteException re) {
            System.out.println("Failed to rebind CloudAtlas");
        }
    }

    public static void rebindSigner(ClientStructures structures) {
        try {
            Registry registry = LocateRegistry.getRegistry(structures.getSignerHost());
            bindSigner(registry, structures);
            System.out.println("Signer rebound");
        } catch (RemoteException re) {
            System.out.println("Failed to rebind Signer");
        }
    }

    public Client(String host, String signerHost, int port, long interval, int maxResultsSize) throws IOException {
        this.port = port;
        this.interval = interval;
        this.maxResultsSize = maxResultsSize;

        this.structures.setHost(host);
        this.structures.setSignerHost(signerHost);

        try {
            Registry registry = LocateRegistry.getRegistry(host);
            bindCloudAtlas(registry, structures);
            Registry signerRegistry = LocateRegistry.getRegistry(signerHost);
            bindSigner(signerRegistry, structures);

        } catch (RemoteException e) {
            System.out.print("Remote exception while locating registry!");
            e.printStackTrace();
        }

        createServer();
    }

    @Override
    public void run() {
        try {
            if (structures.results.size() > maxResultsSize) {
                structures.results.remove(structures.results.firstKey());
            }
            List<String> zones = structures.cloudAtlas.getZones();
            Map<String, AttributesMap> res = new HashMap<>();
            for (String zone : zones) {
                res.put(zone, structures.cloudAtlas.getAttributes(zone));
            }
            structures.results.put(System.currentTimeMillis(), res);

            System.out.println("Results updated");

        } catch (AgentException e) {
            System.out.println("Agent exception during results update!");
            System.out.println(e.getMessage());

        } catch (RemoteException e) {
            System.out.println("Remote exception during results update!");
            System.out.println(e.getMessage());
            rebindCloudAtlas(structures);
        }
    }
}
