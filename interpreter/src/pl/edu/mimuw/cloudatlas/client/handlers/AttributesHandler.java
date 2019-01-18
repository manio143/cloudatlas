package pl.edu.mimuw.cloudatlas.client.handlers;

import pl.edu.mimuw.cloudatlas.agent.agentExceptions.ZoneNotFoundException;
import pl.edu.mimuw.cloudatlas.client.ClientStructures;
import pl.edu.mimuw.cloudatlas.model.Attribute;
import pl.edu.mimuw.cloudatlas.model.AttributesMap;
import pl.edu.mimuw.cloudatlas.model.Value;

import java.util.Map;

public class AttributesHandler extends RMIHandler {
    public AttributesHandler(ClientStructures structures) {
        super(structures);
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
            } catch (Exception e) {
                response = e.getMessage();
            }
        }
        return response;
    }
}
