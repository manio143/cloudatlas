package pl.edu.mimuw.cloudatlas.client.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import pl.edu.mimuw.cloudatlas.agent.utility.Logger;
import pl.edu.mimuw.cloudatlas.client.ClientStructures;
import pl.edu.mimuw.cloudatlas.model.Value;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

import static pl.edu.mimuw.cloudatlas.model.Type.PrimaryType.DOUBLE;
import static pl.edu.mimuw.cloudatlas.model.Type.PrimaryType.INT;

public abstract class RMIHandler implements HttpHandler {
    protected final ClientStructures structures;

    protected final Logger logger;

    public RMIHandler(ClientStructures structures, Logger logger) {
        this.structures = structures;
        this.logger = logger;
    }

    protected abstract String prepareResponse(Map<String, String> parameters) throws RemoteException;

    @SuppressWarnings("unchecked")
    protected void parseQuery(String query, Map<String, String> parameters) throws UnsupportedEncodingException {

        if (query != null) {
            String pairs[] = query.split("[&]");
            for (String pair : pairs) {
                String param[] = pair.split("[=]");
                if (param.length > 1) {
                    String key = URLDecoder.decode(param[0],
                            System.getProperty("file.encoding"));

                    String value = URLDecoder.decode(pair.substring(param[0].length() + 1, pair.length()),
                            System.getProperty("file.encoding"));

                    parameters.put(key, value);
                }
            }
        }
    }

    protected String valueJSON(Value val) {
        if (val.isNull()) {
            return "null";
        } else if (val.getType().getPrimaryType() == INT || val.getType().getPrimaryType() == DOUBLE) {
            return val.toString();
        } else {
            return "\"" + val.toString() + "\"";
        }
    }


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