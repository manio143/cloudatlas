package pl.edu.mimuw.cloudatlas.client.handlers;

import pl.edu.mimuw.cloudatlas.agent.agentExceptions.AgentException;
import pl.edu.mimuw.cloudatlas.agent.utility.Logger;
import pl.edu.mimuw.cloudatlas.client.Client;
import pl.edu.mimuw.cloudatlas.client.ClientStructures;
import pl.edu.mimuw.cloudatlas.signer.SignedQueryRequest;
import pl.edu.mimuw.cloudatlas.signer.signerExceptions.SignerException;

import java.rmi.RemoteException;
import java.util.Map;

public
class InstallHandler extends RMIHandler {
    public InstallHandler(ClientStructures structures) {
        super(structures, new Logger("INSTALL"));
    }

    @Override
    public String prepareResponse(Map<String, String> parameters) {
        String response = "";

        if (!parameters.containsKey("attribute")) {
            response = "Error: attribute not specified!";
        } else if (!parameters.containsKey("query")) {
            response = "Error: query not specified!";
        } else {
            boolean signerResponded = false;
            try {
                String attribute = parameters.get("attribute");
                String select = parameters.get("query");
                String queries = attribute + ": " + select;
                SignedQueryRequest sqr = structures.signer.installQueries(queries);
                signerResponded = true;
                logger.log("Signer accepted the query!");
                structures.cloudAtlas.installQueries(sqr);
                response = "Successful install of " + attribute;
            } catch (AgentException e) {
                response = "AgentException: " + e.getMessage();
            } catch (SignerException e) {
                response = "SignerException: " + e.getMessage();
            } catch (RemoteException e) {
                if (signerResponded) {
                    Client.rebindCloudAtlas(structures, logger);
                } else {
                    Client.rebindSigner(structures, logger);
                }
                response = "RemoteException, trying to rebind!";
            }
        }
        logger.log(response);
        return response;
    }
}