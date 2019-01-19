package pl.edu.mimuw.cloudatlas.client.handlers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;

import pl.edu.mimuw.cloudatlas.agent.utility.Logger;
import pl.edu.mimuw.cloudatlas.client.ClientStructures;
import pl.edu.mimuw.cloudatlas.cloudAtlasAPI.CloudAtlasAPI;

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
            String prevHost = structures.getAgentHost();
            String newHost = parameters.get("host");
            try {
                logger.log("Trying to connect to host: " + newHost);
                InetAddress ia = InetAddress.getByName(newHost);
                structures.setAgentHost(newHost);
                Registry registry = LocateRegistry.getRegistry(structures.getAgentHost());
                structures.cloudAtlas = (CloudAtlasAPI) registry.lookup("CloudAtlasAPI");
                logger.log("CloudAtlas bound");
                response = "Host set to " + newHost;

            } catch (UnknownHostException e) {
                structures.setAgentHost(prevHost);
                response = "Error: host unreachable!";

            } catch (RemoteException e) {
                structures.setAgentHost(prevHost);
                response = "Error: remote exception!";

            } catch (NotBoundException e) {
                structures.setAgentHost(prevHost);
                response = "Error: CloudAtlas not bound!";
            }
        }
        logger.log(response);
        return response;
    }
}