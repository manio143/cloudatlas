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

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.w3c.dom.Attr;
import pl.edu.mimuw.cloudatlas.agent.NotSingletonZoneException;
import pl.edu.mimuw.cloudatlas.agent.ZoneNotFoundException;
import pl.edu.mimuw.cloudatlas.cloudAtlasAPI.CloudAtlasAPI;
import pl.edu.mimuw.cloudatlas.model.*;

import static pl.edu.mimuw.cloudatlas.model.Type.PrimaryType.INT;

public class ClientServer {
    private CloudAtlasAPI stub;
    private Integer port;
    private String host;

    public ClientServer(String host, String port) {
        this.host = host;
        this.port = Integer.parseInt(port);
    }

    public static void main(String[] args) {
        ClientServer server = new ClientServer(args[0], args[1]);
        server.run();
    }

    private void run() {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            stub = (CloudAtlasAPI) registry.lookup("CloudAtlasAPI");
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/zones", new ZonesHandler());
            server.createContext("/attributes", new AttributesHandler());
            server.setExecutor(null);
            server.start();

        } catch (Exception e) {
            System.err.println("Client server exception:");
            e.printStackTrace();
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
                response += zones.get(i);
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
                    AttributesMap attributes = stub.getAttributes((String)parameters.get("path"));
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
}