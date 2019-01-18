package pl.edu.mimuw.cloudatlas.client.handlers;

import pl.edu.mimuw.cloudatlas.agent.utility.Logger;
import pl.edu.mimuw.cloudatlas.client.ClientStructures;
import pl.edu.mimuw.cloudatlas.model.AttributesMap;
import pl.edu.mimuw.cloudatlas.model.Value;

import java.util.Map;

public class OvertimeHandler extends RMIHandler {
    public OvertimeHandler(ClientStructures structures) {
        super(structures, new Logger("OVERTIME"));
    }

    @Override
    public String prepareResponse(Map<String, String> parameters) {
        StringBuilder response = new StringBuilder();
        response.append("{\n");

        if (!parameters.containsKey("path")) {
            return "Error: path not specified!";
        } else if (!parameters.containsKey("attribute")) {
            return "Error: attribute not specified!";
        } else {
            String path = parameters.get("path");
            String attribute = parameters.get("attribute");
            response.append("\t\"Values\" : [");
            for (Map.Entry<Long, Map<String, AttributesMap>> entry : structures.results.entrySet()) {
                if (entry.getValue().containsKey(path)) {
                    AttributesMap map = entry.getValue().get(path);
                    Value val = map.getOrNull(attribute);
                    if (val != null) {
                        response.append("\n\t\t{\"timestamp\" : " + entry.getKey() + ", ");
                        response.append("\"value\" : " + val + "},");
                    }
                }
            }
            if (response.charAt(response.length() - 1) == ',') {
                response.replace(response.length() - 1, response.length(), "");
            }
            response.append("\n\t]");
        }

        response.append("\n}");
        return response.toString();
    }
}
