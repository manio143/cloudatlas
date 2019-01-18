package pl.edu.mimuw.cloudatlas.client.handlers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import pl.edu.mimuw.cloudatlas.agent.utility.Logger;
import pl.edu.mimuw.cloudatlas.client.Client;
import pl.edu.mimuw.cloudatlas.client.ClientStructures;

public class SetHostHandler extends RMIHandler {
    public SetHostHandler(ClientStructures structures) {
        super(structures, new Logger("SET_HOST"));
    }

    @Override
    public String prepareResponse(Map<String, String> parameters) {
        String response = "";
        if (!parameters.containsKey("host")) {
            response = "Error: host not specified!";
        } else {
            String newHost = parameters.get("host");
            try {
                logger.log("Trying to connect to host: " + newHost);
                InetAddress ia = InetAddress.getByName(newHost);
                structures.setHost(newHost);
                Client.rebindCloudAtlas(structures, logger);
                response = "Host set to " + newHost;
            } catch (UnknownHostException e) {
                response = "Error: host unreachable!";
            }
        }
        logger.log(response);
        return response;
    }
}