package pl.edu.mimuw.cloudatlas.client.handlers;

import pl.edu.mimuw.cloudatlas.client.ClientStructures;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public class ZonesHandler extends RMIHandler {
    public ZonesHandler(ClientStructures structures) {
        super(structures);
    }

    @Override
    public String prepareResponse(Map<String, String> parameters) throws RemoteException {
        String response = "{\n" +
                "\t\"Zones\": [";

        List<String> zones = structures.cloudAtlas.getZones();

        for (int i = 0; i < zones.size(); i++) {
            if (i != 0) {
                response += ", ";
            }
            response += "\"" + zones.get(i) + "\"";
        }

        response += "]\n}";
        return response;
    }
}