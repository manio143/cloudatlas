package pl.edu.mimuw.cloudatlas.client.handlers;

import pl.edu.mimuw.cloudatlas.agent.agentExceptions.AgentException;
import pl.edu.mimuw.cloudatlas.agent.utility.Logger;
import pl.edu.mimuw.cloudatlas.client.Client;
import pl.edu.mimuw.cloudatlas.client.ClientStructures;
import pl.edu.mimuw.cloudatlas.model.ModelReader;
import pl.edu.mimuw.cloudatlas.model.Value;

import java.rmi.RemoteException;
import java.util.Map;

public class SetAttributeHandler extends RMIHandler {
    public SetAttributeHandler(ClientStructures structures) {
        super(structures, new Logger("SET_ATTRIBUTE"));
    }

    @Override
    public String prepareResponse(Map<String, String> parameters) {
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
                Value val = ModelReader.formValue(parameters.get("type"), valueString);
                structures.cloudAtlas.setAttribute(parameters.get("path"), parameters.get("name"), val);
                response = "Attribute changed!";
            } catch (AgentException e) {
                response = e.getMessage();
            } catch (RemoteException e) {
                Client.rebindCloudAtlas(structures, logger);
                response = "RemoteException, trying to rebind!";
            }
        }

        logger.log(response);
        return response;
    }
}