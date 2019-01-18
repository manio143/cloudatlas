package pl.edu.mimuw.cloudatlas.client.handlers;

import pl.edu.mimuw.cloudatlas.agent.agentExceptions.AgentException;
import pl.edu.mimuw.cloudatlas.client.Client;
import pl.edu.mimuw.cloudatlas.client.ClientStructures;
import pl.edu.mimuw.cloudatlas.signer.SignedQueryRequest;

import java.rmi.RemoteException;
import java.util.Map;

public class UninstallHandler extends RMIHandler {
    public UninstallHandler(ClientStructures structures) {
        super(structures);
    }

    @Override
    public String prepareResponse(Map<String, String> parameters) {
        String response = "";

        if (!parameters.containsKey("attribute")) {
            response = "Error: attribute not specified!";
        } else {
            boolean signerResponded = false;
            try {
                SignedQueryRequest sqr = structures.signer.uninstallQueries(parameters.get("attribute"));
                signerResponded = true;
                structures.cloudAtlas.uninstallQuery(sqr);
                response = "Successful uninstall of " + parameters.get("attribute");
            } catch (AgentException e) {
                response = "AgentException: " + e.getMessage();
            } catch (RemoteException e) {
                if (signerResponded) {
                    Client.rebindCloudAtlas(structures);
                } else {
                    Client.rebindSigner(structures);
                }
                response = "RemoteException, trying to rebind!";
            }
        }

        return response;
    }
}