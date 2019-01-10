package pl.edu.mimuw.cloudatlas.signer;

import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.io.File;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import pl.edu.mimuw.cloudatlas.cloudAtlasAPI.SignerAPI;

public class KeyGenerator {
    /*
     * Generate Public and Private keys and serialize them to files provided as
     * arguments to the program.
     * 
     * Usage: KeyGenerator path/to/public path/to/private
     */
    public static void main(String[] args) {
        try {
            KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
            keyGenerator.initialize(1024);
            KeyPair keyPair = keyGenerator.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            byte[] privBytes = privateKey.getEncoded();
            byte[] pubBytes = publicKey.getEncoded();

            Files.write(new File(args[0]).toPath(), pubBytes, StandardOpenOption.WRITE);
            Files.write(new File(args[1]).toPath(), privBytes, StandardOpenOption.WRITE);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
}