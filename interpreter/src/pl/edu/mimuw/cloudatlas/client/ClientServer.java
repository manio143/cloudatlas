package pl.edu.mimuw.cloudatlas.client;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.w3c.dom.Attr;
import pl.edu.mimuw.cloudatlas.agent.ZoneNotFoundException;
import pl.edu.mimuw.cloudatlas.cloudAtlasAPI.CloudAtlasAPI;
import pl.edu.mimuw.cloudatlas.model.*;

import static pl.edu.mimuw.cloudatlas.model.Type.PrimaryType.DOUBLE;
import static pl.edu.mimuw.cloudatlas.model.Type.PrimaryType.INT;

public class ClientServer implements Runnable {
    private CloudAtlasAPI stub;

    final int MAX_SIZE = 100;
    TreeMap<Long,Map<String, AttributesMap>> results = new TreeMap<>();

    public ClientServer(String host, String port) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            stub = (CloudAtlasAPI) registry.lookup("CloudAtlasAPI");
            HttpServer server = HttpServer.create(new InetSocketAddress(Integer.parseInt(port)), 0);
            server.createContext("/all", new AllZonesHandler());
            server.createContext("/zones", new ZonesHandler());
            server.createContext("/attributes", new AttributesHandler());
            server.createContext("/", new FileHandler("www/table.html"));
            server.createContext("/bootstrap.min.js", new FileHandler("www/bootstrap.min.js"));
            server.createContext("/bootstrap.min.css", new FileHandler("www/bootstrap.min.css"));
            server.createContext("/jquery-3.3.1.min.js", new FileHandler("www/jquery-3.3.1.min.js"));
            server.createContext("/jquery.treetable.js", new FileHandler("www/jquery.treetable.js"));
            server.createContext("/jquery.treetable.css", new FileHandler("www/jquery.treetable.css"));
            server.createContext("/jquery.treetable.theme.default.css", new FileHandler("www/jquery.treetable.theme.default.css"));
            server.setExecutor(null);
            server.start();

        } catch (Exception e) {
            System.err.println("Client server exception:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream(args[2]));
            Long interval = Long.parseLong(prop.getProperty("collection_interval"));
            ClientServer server = new ClientServer(args[0], args[1]);
            ScheduledExecutorService scheduler =
                    Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(server, 0L, interval, TimeUnit.SECONDS);
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void run() {
        try {
            if (results.size() > MAX_SIZE) {
                results.remove(results.firstKey());
            }
            List<String> zones = stub.getZones();
            Map<String, AttributesMap> res = new HashMap<>();
            for (String zone : zones) {
                res.put(zone, stub.getAttributes(zone));
            }
            results.put(System.currentTimeMillis(), res);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static void parseQuery(String query, Map<String,
            Object> parameters) throws UnsupportedEncodingException {

        if (query != null) {
            String pairs[] = query.split("[&]");
            for (String pair : pairs) {
                String param[] = pair.split("[=]");
                String key = null;
                String value = null;
                if (param.length > 0) {
                    key = URLDecoder.decode(param[0],
                            System.getProperty("file.encoding"));
                }

                if (param.length > 1) {
                    value = URLDecoder.decode(param[1],
                            System.getProperty("file.encoding"));
                }

                if (parameters.containsKey(key)) {
                    Object obj = parameters.get(key);
                    if (obj instanceof List<?>) {
                        List<String> values = (List<String>) obj;
                        values.add(value);

                    } else if (obj instanceof String) {
                        List<String> values = new ArrayList<String>();
                        values.add((String) obj);
                        values.add(value);
                        parameters.put(key, values);
                    }
                } else {
                    parameters.put(key, value);
                }
            }
        }
    }

    String resultsJSON() {
        StringBuilder response = new StringBuilder();
        response.append("{\n");
        Integer which = 0;
        for (Map.Entry<Long, Map<String, AttributesMap>> entry : results.entrySet()) {
            response.append("\t\"Fetch_" + which + "\": {\n");
            which++;
            response.append("\t\t\"Timestamp\" : " + entry.getKey() + ",\n");
            response.append("\t\t\"Map\" : {\n");

            for (Map.Entry<String, AttributesMap> zone : entry.getValue().entrySet()) {
                response.append("\t\t\t\"" + zone.getKey() + "\" : {\n");
                for (Map.Entry<Attribute, Value> attribute : zone.getValue()) {
                    Value val = attribute.getValue();
                    response.append("\t\t\t\t\"" + attribute.getKey() + "\" : ");
                    if (val.getType().getPrimaryType() == INT || val.getType().getPrimaryType() == DOUBLE) {
                        response.append(val.toString());
                    } else {
                        response.append("\"" + val.toString() + "\"");
                    }
                    response.append(",\n");
                }
                response.replace(response.length() - 2, response.length() - 1, "");
                response.append("\t\t\t},\n");
            }
            response.replace(response.length() - 2, response.length() - 1, "");
            response.append("\t\t}\n");
            response.append("\t},\n");
        }
        response.replace(response.length() - 2, response.length() - 1, "");

        response.append("}");
        return response.toString();
    }

    class AllZonesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = resultsJSON();

            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    class ZonesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "{\n" +
                    "\t\"Zones\": [";

            List<String> zones = stub.getZones();

            for (int i = 0; i < zones.size(); i++) {
                if (i != 0) {
                    response += ", ";
                }
                response += "\"" + zones.get(i) + "\"";
            }

            response += "]\n}";

            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    class AttributesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            Map<String, Object> parameters = new HashMap<String, Object>();
            URI requestedUri = t.getRequestURI();
            String query = requestedUri.getRawQuery();

            parseQuery(query, parameters);

            String response = "{\n";
            if (!parameters.containsKey("path")) {
                response = "Error: path not specified!";
            } else {
                try {
                    AttributesMap attributes = stub.getAttributes((String) parameters.get("path"));
                    for (Map.Entry<Attribute, Value> entry : attributes) {
                        Attribute attr = entry.getKey();
                        Value val = entry.getValue();
                        response += "\t\"" + attr + "\": ";
                        String value = "";
                        if (val.getType().getPrimaryType() == INT) {
                            value = val.toString();
                        } else {
                            value = "\"" + val.toString() + "\"";
                        }
                        response += value + ",\n";
                    }
                    response = response.substring(0, response.length() - 2);
                    response += "\n}";
                } catch (ZoneNotFoundException e) {
                    response = "Error: no node under that path";
                } catch (Exception e) {
                    response = e.getMessage();
                }
            }

            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    
    class FileHandler implements HttpHandler {
        String path;

        public FileHandler(String path) {
            this.path = path;
        }

        @Override
        public void handle(HttpExchange t) throws IOException {
            byte[] response = Files.readAllBytes(Paths.get(path));

            t.sendResponseHeaders(200, response.length);
            OutputStream os = t.getResponseBody();
            os.write(response);
            os.close();
        }
    }
}