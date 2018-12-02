package pl.edu.mimuw.cloudatlas.agent;

import pl.edu.mimuw.cloudatlas.cloudAtlasAPI.CloudAtlasAPI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class CloudAtlasServer {
    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            CloutAtlasAgent object = new CloutAtlasAgent(args[0]);
            CloudAtlasAPI stub =
                    (CloudAtlasAPI) UnicastRemoteObject.exportObject(object, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("CloudAtlasAPI", stub);
            System.out.println("CloutAtlasAgent bound");
        } catch (Exception e) {
            System.err.println("Agent exception:");
            e.printStackTrace();
        }
    }
}
