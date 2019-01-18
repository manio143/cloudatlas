package pl.edu.mimuw.cloudatlas.client.handlers;

import pl.edu.mimuw.cloudatlas.agent.agentExceptions.AgentException;
import pl.edu.mimuw.cloudatlas.client.ClientStructures;

import java.util.List;
import java.util.Map;

public class QueriesHandler extends RMIHandler {
    public QueriesHandler(ClientStructures structures) {
        super(structures);
    }

    @Override
    public String prepareResponse(Map<String, String> parameters) {
        StringBuilder response = new StringBuilder();
        response.append("{\n");
        response.append("\t\"Queries\" : [");

        try {
            Map<String, List<String>> queries = structures.cloudAtlas.getQueries();

            for (Map.Entry<String, List<String>> entry : queries.entrySet()) {
                response.append("\n\t\t{\"name\" : \"" + entry.getKey() + "\", ");
                response.append("\"columns\" : [");
                for (String attr : entry.getValue()) {
                    response.append("\"" + attr + "\", ");
                }
                if (response.charAt(response.length() - 2) == ',') {
                    response.replace(response.length() - 2, response.length(), "");
                }
                response.append("]},");
            }
            if (response.charAt(response.length() - 1) == ',') {
                response.replace(response.length() - 1, response.length(), "");
            }

        } catch (AgentException e) {
            return "AgentException: " + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }

        response.append("\n\t]");
        response.append("\n}");
        return response.toString();
    }
}