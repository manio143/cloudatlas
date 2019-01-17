package pl.edu.mimuw.cloudatlas.client;

import pl.edu.mimuw.cloudatlas.agent.QueueKeeper;
import pl.edu.mimuw.cloudatlas.cloudAtlasAPI.CloudAtlasAPI;
import pl.edu.mimuw.cloudatlas.model.AttributesMap;
import pl.edu.mimuw.cloudatlas.model.ValueInt;
import pl.edu.mimuw.cloudatlas.model.ValueSet;
import pl.edu.mimuw.cloudatlas.signer.SignedQueryRequest;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.util.List;

import static pl.edu.mimuw.cloudatlas.model.TypePrimitive.CONTACT;

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

            cloudAtlas.getAttributes("/");
            cloudAtlas.getQueries();
            cloudAtlas.setAttribute("/uw/violet07", "wan", new ValueInt(3L));

            cloudAtlas.setFallbackContacts(new ValueSet(CONTACT));


        } catch (Exception e) {
            System.err.println("TestClient exception:");
            e.printStackTrace();
        }
    }
}
