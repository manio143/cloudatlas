package pl.edu.mimuw.cloudatlas.client.handlers;

import pl.edu.mimuw.cloudatlas.client.ClientStructures;
import pl.edu.mimuw.cloudatlas.model.Attribute;
import pl.edu.mimuw.cloudatlas.model.AttributesMap;
import pl.edu.mimuw.cloudatlas.model.Value;

import java.util.Map;

public class AllZonesHandler extends RMIHandler {
    public AllZonesHandler(ClientStructures structures) {
        super(structures);
    }

    @Override
    public String prepareResponse(Map<String, String> parameters) {
        StringBuilder response = new StringBuilder();
        response.append("{\n");
        Integer which = 0;
        for (Map.Entry<Long, Map<String, AttributesMap>> entry : structures.results.entrySet()) {
            response.append("\t\"Fetch_" + which + "\": {\n");
            which++;
            response.append("\t\t\"Timestamp\" : " + entry.getKey() + ",\n");
            response.append("\t\t\"Map\" : {\n");

            for (Map.Entry<String, AttributesMap> zone : entry.getValue().entrySet()) {
                response.append("\t\t\t\"" + zone.getKey() + "\" : {\n");
                for (Map.Entry<Attribute, Value> attribute : zone.getValue()) {
                    Value val = attribute.getValue();
                    response.append("\t\t\t\t\"" + attribute.getKey() + "\" : " + valueJSON(val) + ",\n");
                }
                if (response.charAt(response.length() - 2) == ',') {
                    response.replace(response.length() - 2, response.length() - 1, "");
                }
                response.append("\t\t\t},\n");
            }
            if (response.charAt(response.length() - 2) == ',') {
                response.replace(response.length() - 2, response.length() - 1, "");
            }
            response.append("\t\t}\n");
            response.append("\t},\n");
        }
        if (response.charAt(response.length() - 2) == ',') {
            response.replace(response.length() - 2, response.length() - 1, "");
        }
        response.append("}");
        return response.toString();
    }
}