package pl.edu.mimuw.cloudatlas.client.handlers;

import pl.edu.mimuw.cloudatlas.agent.agentExceptions.AgentException;
import pl.edu.mimuw.cloudatlas.agent.agentExceptions.ZoneNotFoundException;
import pl.edu.mimuw.cloudatlas.agent.utility.Logger;
import pl.edu.mimuw.cloudatlas.client.Client;
import pl.edu.mimuw.cloudatlas.client.ClientStructures;
import pl.edu.mimuw.cloudatlas.model.Attribute;
import pl.edu.mimuw.cloudatlas.model.AttributesMap;
import pl.edu.mimuw.cloudatlas.model.Value;

import java.rmi.RemoteException;
import java.util.Map;

public class AttributesHandler extends RMIHandler {
    public AttributesHandler(ClientStructures structures) {
        super(structures, new Logger("ATTRIBUTES"));
    }

    @Override
    public String prepareResponse(Map<String, String> parameters) {
        String response = "{\n";
        if (!parameters.containsKey("path")) {
            response = "Error: path not specified!";
        } else {
            try {
                AttributesMap attributes = structures.cloudAtlas.getAttributes(parameters.get("path"));
                for (Map.Entry<Attribute, Value> entry : attributes) {
                    Attribute attr = entry.getKey();
                    Value val = entry.getValue();
                    response += "\t\"" + attr + "\": " + valueJSON(val) + ",\n";
                }
                response = response.substring(0, response.length() - 2);
                response += "\n}";
            } catch (ZoneNotFoundException e) {
                response = "Error: no node under that path";
            } catch (AgentException e) {
                response = e.getMessage();
            } catch (RemoteException e) {
                Client.rebindCloudAtlas(structures, logger);
                response = "RemoteException, trying to rebind!";
            }
        }
        return response;
    }
}
