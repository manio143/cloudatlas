package pl.edu.mimuw.cloudatlas.client.handlers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import pl.edu.mimuw.cloudatlas.client.Client;
import pl.edu.mimuw.cloudatlas.client.ClientStructures;

public class SetHostHandler extends RMIHandler {
    public SetHostHandler(ClientStructures structures) {
        super(structures);
    }

    @Override
    public String prepareResponse(Map<String, String> parameters) {
        String response = "";
        if (!parameters.containsKey("host")) {
            response = "Error: host not specified!";
        } else {
            String newHost = parameters.get("host");
            try {
                InetAddress ia = InetAddress.getByName(newHost);
                structures.setHost(newHost);
                Client.rebindCloudAtlas(structures);
                response = "Host set to " + newHost;
            } catch (UnknownHostException e) {
                response = "Error: host unreachable!";
            }
        }
        return response;
    }
}