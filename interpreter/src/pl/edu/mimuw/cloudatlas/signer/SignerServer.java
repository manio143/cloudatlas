package pl.edu.mimuw.cloudatlas.signer;

import java.nio.file.Files;
import java.io.File;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.X509EncodedKeySpec;

import pl.edu.mimuw.cloudatlas.cloudAtlasAPI.SignerAPI;

public class SignerServer {
    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            byte[] keyBytes = Files.readAllBytes(new File(args[0]).toPath());
            X509EncodedKeySpec kspec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            SignerAgent object = new SignerAgent(kf.generatePrivate(kspec));
            SignerAPI stub =
                    (SignerAPI) UnicastRemoteObject.exportObject(object, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("SignerAPI", stub);
            System.out.println("SignerAgent bound");
        } catch (Exception e) {
            System.err.println("Signer exception:");
            e.printStackTrace();
        }
    }
}