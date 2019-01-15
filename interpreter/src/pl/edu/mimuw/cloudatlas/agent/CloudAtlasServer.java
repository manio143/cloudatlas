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
            prop.load(new FileInputStream(args[1]));

            byte[] keyBytes = Files.readAllBytes(new File(args[2]).toPath());
            X509EncodedKeySpec kspec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            CloudAtlasAgent agent = new CloudAtlasAgent(args[0], kf.generatePublic(kspec));

            CloudAtlasPool threadPool = new CloudAtlasPool(agent, prop);

            threadPool.runModules();

        } catch (Exception e) {
            System.err.println("Agent exception:");
            e.printStackTrace();
        }
    }
}
