package pl.edu.mimuw.cloudatlas.agent;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.io.File;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Properties;

public class CloudAtlasServer {
    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream(args[0]));

            byte[] keyBytes = Files.readAllBytes(new File(args[1]).toPath());
            X509EncodedKeySpec kspec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            String nodePath = prop.getProperty("nodePath");
            String contacts = prop.getProperty("contacts");
            String fallbackContacts = prop.getProperty("fallbackContacts");

            CloudAtlasAgent agent = new CloudAtlasAgent(nodePath, contacts, fallbackContacts, kf.generatePublic(kspec));

            CloudAtlasPool threadPool = new CloudAtlasPool(agent, prop);

            threadPool.runModules();

        } catch (Exception e) {
            System.err.println("Agent exception:");
            e.printStackTrace();
        }
    }
}
