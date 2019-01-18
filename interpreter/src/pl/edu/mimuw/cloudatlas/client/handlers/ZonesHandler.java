package pl.edu.mimuw.cloudatlas.client.handlers;

import pl.edu.mimuw.cloudatlas.agent.agentExceptions.AgentException;
import pl.edu.mimuw.cloudatlas.agent.utility.Logger;
import pl.edu.mimuw.cloudatlas.client.Client;
import pl.edu.mimuw.cloudatlas.client.ClientStructures;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public class ZonesHandler extends RMIHandler {
    public ZonesHandler(ClientStructures structures) {
        super(structures, new Logger("ZONES"));
    }

    @Override
    public String prepareResponse(Map<String, String> parameters) {
        String response = "{\n" + "\t\"Zones\": [";
        try {

            List<String> zones = structures.cloudAtlas.getZones();

            for (int i = 0; i < zones.size(); i++) {
                if (i != 0) {
                    response += ", ";
                }
                response += "\"" + zones.get(i) + "\"";
            }
            response += "]\n}";
            return response;

        } catch (AgentException e) {
            response = "AgentException: " + e.getMessage();
            logger.log(response);
        } catch (RemoteException e) {
            response = "RemoteException, trying to rebind!";
            logger.log(response);
            Client.rebindCloudAtlas(structures, logger);
        }

        return response;
    }
}