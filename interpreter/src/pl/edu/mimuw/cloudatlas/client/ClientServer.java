package pl.edu.mimuw.cloudatlas.client;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import pl.edu.mimuw.cloudatlas.agent.AgentException;
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
            server.createContext("/", new FileHandler("www/table.html"));
            server.createContext("/queries", new FileHandler("www/queries.html"));
            server.createContext("/set", new FileHandler("www/setAttribute.html"));
            server.createContext("/contacts", new FileHandler("www/fallback.html"));
            server.createContext("/rmi/all", new AllZonesHandler());
            server.createContext("/rmi/overtime", new OvertimeHandler());
            server.createContext("/rmi/zones", new ZonesHandler());
            server.createContext("/rmi/contacts", new ContactsHandler());
            server.createContext("/rmi/set", new SetAttributeHandler());
            server.createContext("/rmi/attributes", new AttributesHandler());
            server.createContext("/rmi/install", new InstallHandler());
            server.createContext("/rmi/uninstall", new UninstallHandler());
            server.createContext("/graph.js", new FileHandler("www/graph.js"));
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
    private static void parseQuery(String query, Map<String, String> parameters) throws UnsupportedEncodingException {

        if (query != null) {
            String pairs[] = query.split("[&]");
            for (String pair : pairs) {
                String param[] = pair.split("[=]");
                String key = null;
                String value = null;

                if (param.length > 1) {
                    key = URLDecoder.decode(param[0],
                            System.getProperty("file.encoding"));

                    value = URLDecoder.decode(pair.substring(param[0].length() + 1, pair.length()),
                            System.getProperty("file.encoding"));

                    parameters.put(key, value);
                }
            }
        }
    }

    private abstract class RMIHandler implements HttpHandler {
        public abstract String prepareResponse(Map<String, String> parameters) throws RemoteException;

        @Override
        public void handle(HttpExchange t) throws IOException {
            Map<String, String> parameters = new HashMap<>();
            URI requestedUri = t.getRequestURI();
            String query = requestedUri.getRawQuery();

            parseQuery(query, parameters);

            String response = prepareResponse(parameters);

            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    private class AllZonesHandler extends RMIHandler {
        @Override
        public String prepareResponse(Map<String, String> parameters) {
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
    }

    class OvertimeHandler extends RMIHandler {
        @Override
        public String prepareResponse(Map<String, String> parameters) {
            StringBuilder response = new StringBuilder();
            response.append("{\n");

            if (!parameters.containsKey("path")) {
                return "Error: path not specified!";
            } else if (!parameters.containsKey("attribute")) {
                return "Error: attribute not specified!";
            } else {
                String path = parameters.get("path");
                String attribute = parameters.get("attribute");
                response.append("\t\"Values\" : [");
                for (Map.Entry<Long, Map<String, AttributesMap>> entry : results.entrySet()) {
                    if (entry.getValue().containsKey(path)) {
                        AttributesMap map = entry.getValue().get(path);
                        Value val = map.getOrNull(attribute);
                        if (val != null) {
                            response.append("{\"timestamp\" : " + entry.getKey() + ", ");
                            response.append("\"value\" : " + val + "},");
                        }
                    }
                }
                response.replace(response.length() - 1, response.length(), "");
                response.append("]\n");
            }

            response.append("}");
            return response.toString();
        }
    }

    class ZonesHandler extends RMIHandler {
        @Override
        public String prepareResponse(Map<String, String> parameters) throws RemoteException {
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
            return response;
        }
    }

    class ContactsHandler extends RMIHandler {
        @Override
        public String prepareResponse(Map<String, String> parameters) throws RemoteException {
            String response = "";
            if (!parameters.containsKey("set")) {
                response = "Error: contacts not specified!";
            } else {
                try {
                    ValueSet contacts = (ValueSet)ValueReader.formValue("set contact", "{" + parameters.get("set") + "}");
                    stub.setFallbackContacts(contacts);
                    response = "Successfully set fallback contacts";
                } catch (Exception e) {
                    response = "Error: " + e.getMessage();
                }
            }

            return response;
        }
    }

    class AttributesHandler extends RMIHandler {
        @Override
        public String prepareResponse(Map<String, String> parameters) {
            String response = "{\n";
            if (!parameters.containsKey("path")) {
                response = "Error: path not specified!";
            } else {
                try {
                    AttributesMap attributes = stub.getAttributes(parameters.get("path"));
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
            return response;
        }
    }

    class SetAttributeHandler extends RMIHandler {
        @Override
        public String prepareResponse(Map<String, String> parameters) throws RemoteException {
            String response = "";
            if (!parameters.containsKey("path")) {
                response = "Error: node path not specified!";
            } else if (!parameters.containsKey("name")) {
                response = "Error: attribute name not specified!";
            } else if (!parameters.containsKey("type")) {
                response = "Error: type not specified!";
            } else if (!parameters.containsKey("value")) {
                response = "Error: value not specified!";
            } else {
                try {
                    String valueString =
                            parameters.get("value")
                                    .replace("<", "{")
                                    .replace(">", "}");
                    Value val = ValueReader.formValue(parameters.get("type"), valueString);
                    stub.setAttribute(parameters.get("path"), parameters.get("name"), val);
                    response = "Attribute changed!";
                } catch(AgentException e) {
                    response = e.getMessage();
                }
            }

            return response;
        }
    }

    class UninstallHandler extends RMIHandler {
        @Override
        public String prepareResponse(Map<String, String> parameters) {
            String response = "";

            if (!parameters.containsKey("attribute")) {
                response = "Error: attribute not specified!";
            } else {
                try {
                    stub.uninstallQuery(parameters.get("attribute"));
                    response = "Successful uninstall of " + parameters.get("attribute");
                } catch (AgentException e) {
                    response = "AgentException: " + e.getMessage();
                } catch (Exception e) {
                    response = e.getMessage();
                }
            }

            return response;
        }
    }

    class InstallHandler extends RMIHandler {
        @Override
        public String prepareResponse(Map<String, String> parameters) {
            String response = "";

            if (!parameters.containsKey("attribute")) {
                response = "Error: attribute not specified!";
            } else if (!parameters.containsKey("query")) {
                response = "Error: query not specified!";
            } else {
                try {
                    String attribute = parameters.get("attribute");
                    String select = parameters.get("query");
//                    System.out.println(attribute);
//                    System.out.println(select);
                    String queries = "&" + attribute + ": " + select;
//                    System.out.println(queries);
                    stub.installQueries(queries);
                    response = "Successful install of " + attribute;
                } catch (AgentException e) {
                    response = "AgentException: " + e.getMessage();
                } catch (Exception e) {
                    e.printStackTrace();
                    response = "Error: " + e.getMessage();
                }
            }
            return response;
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