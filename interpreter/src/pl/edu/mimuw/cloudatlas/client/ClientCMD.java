package pl.edu.mimuw.cloudatlas.client;

import pl.edu.mimuw.cloudatlas.model.AttributesMap;
import pl.edu.mimuw.cloudatlas.cloudAtlasAPI.CloudAtlasAPI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class ClientCMD {
    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            Registry registry = LocateRegistry.getRegistry(args[0]);
            CloudAtlasAPI stub = (CloudAtlasAPI) registry.lookup("CloudAtlasAPI");
            Scanner scanner = new Scanner(System.in);
            scanner.useDelimiter("\\n");
            while(scanner.hasNext()) {
                stub.installQueries(scanner.next());

            }
            scanner.close();

//            stub.uninstallQuery("two_plus_two");

            List<String> zones = stub.getZones();
            for(String zone : zones) {
                AttributesMap map = stub.getAttributes(zone);
                System.out.println(zone + ": " + map);
            }


        } catch (Exception e) {
            System.err.println("ClientCMD exception:");
            e.printStackTrace();
        }
    }
}
