package pl.edu.mimuw.cloudatlas.agent;

import pl.edu.mimuw.cloudatlas.agent.agentModules.MessageHandler;
import pl.edu.mimuw.cloudatlas.agent.agentModules.ModuleMessage;
import pl.edu.mimuw.cloudatlas.agent.agentModules.TimerModule;

import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.*;

public class CloudAtlasPool {
    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream(args[1]));
            Long interval = Long.parseLong(prop.getProperty("computationInterval"));

            LinkedBlockingQueue<ModuleMessage> timerQueue = new LinkedBlockingQueue<>();
            LinkedBlockingQueue<ModuleMessage> communicationQueue = new LinkedBlockingQueue<>();
            LinkedBlockingQueue<ModuleMessage> rmiQueue = new LinkedBlockingQueue<>();

            List<LinkedBlockingQueue<ModuleMessage>> queues = new ArrayList<>();
            queues.add(timerQueue);
            queues.add(communicationQueue);
            queues.add(rmiQueue);

            MessageHandler messageHandler = new MessageHandler(queues);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(new TimerModule(messageHandler, timerQueue));

        } catch (Exception e) {
            System.err.println("Agent exception:");
            e.printStackTrace();
        }
    }
}
