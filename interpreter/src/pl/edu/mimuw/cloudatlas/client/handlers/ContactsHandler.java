package pl.edu.mimuw.cloudatlas.client.handlers;

import pl.edu.mimuw.cloudatlas.agent.agentExceptions.AgentException;
import pl.edu.mimuw.cloudatlas.client.Client;
import pl.edu.mimuw.cloudatlas.client.ClientStructures;
import pl.edu.mimuw.cloudatlas.model.ModelReader;
import pl.edu.mimuw.cloudatlas.model.ValueSet;

import java.rmi.RemoteException;
import java.util.Map;

public class ContactsHandler extends RMIHandler {
    public ContactsHandler(ClientStructures structures) {
        super(structures);
    }

    @Override
    public String prepareResponse(Map<String, String> parameters) {
        String response = "";
        if (!parameters.containsKey("set")) {
            response = "Error: contacts not specified!";
        } else {
            try {
                ValueSet contacts =
                        (ValueSet)ModelReader
                                .formValue("set contact", "{" + parameters.get("set") + "}");
                structures.cloudAtlas.setFallbackContacts(contacts);
                response = "Successfully set fallback contacts";
            } catch (AgentException e) {
                response = "Error: " + e.getMessage();
            } catch (RemoteException e) {
                Client.rebindCloudAtlas(structures);
                response = "RemoteException, trying to rebind!";
            }
        }

        return response;
    }
}
