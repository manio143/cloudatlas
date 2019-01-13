package pl.edu.mimuw.cloudatlas.client;

import pl.edu.mimuw.cloudatlas.cloudAtlasAPI.CloudAtlasAPI;
import pl.edu.mimuw.cloudatlas.model.AttributesMap;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class TestClient {
    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            Registry registry = LocateRegistry.getRegistry(args[0]);
            CloudAtlasAPI cloudAtlas = (CloudAtlasAPI) registry.lookup("CloudAtlasAPI");

            System.out.println("Zones:");

            List<String> zones = cloudAtlas.getZones();
            System.out.println(zones.size());

            for (String zone : zones) {
                System.out.println(zone);
            }


        } catch (Exception e) {
            System.err.println("TestClient exception:");
            e.printStackTrace();
        }
    }
}
