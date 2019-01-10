package pl.edu.mimuw.cloudatlas.client;

import pl.edu.mimuw.cloudatlas.model.AttributesMap;
import pl.edu.mimuw.cloudatlas.cloudAtlasAPI.CloudAtlasAPI;
import pl.edu.mimuw.cloudatlas.cloudAtlasAPI.SignerAPI;
import pl.edu.mimuw.cloudatlas.signer.SignedQueryRequest;

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
            CloudAtlasAPI cloudAtlas = (CloudAtlasAPI) registry.lookup("CloudAtlasAPI");
            SignerAPI signer = (SignerAPI) registry.lookup("SignerAPI");
            Scanner scanner = new Scanner(System.in);
            scanner.useDelimiter("\\n");
            while(scanner.hasNext()) {
                SignedQueryRequest sqr = signer.uninstallQueries(scanner.next());
                cloudAtlas.installQueries(sqr);

            }
            scanner.close();

            List<String> zones = cloudAtlas.getZones();
            for(String zone : zones) {
                AttributesMap map = cloudAtlas.getAttributes(zone);
                System.out.println(zone + ": " + map);
            }


        } catch (Exception e) {
            System.err.println("ClientCMD exception:");
            e.printStackTrace();
        }
    }
}
