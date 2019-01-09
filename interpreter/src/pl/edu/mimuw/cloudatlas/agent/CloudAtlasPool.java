package pl.edu.mimuw.cloudatlas.agent;

import pl.edu.mimuw.cloudatlas.agent.agentModules.MessageHandler;
import pl.edu.mimuw.cloudatlas.agent.agentModules.ModuleMessage;
import pl.edu.mimuw.cloudatlas.agent.agentModules.TimerModule;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CloudAtlasPool {
//    private static final int THREADS_NUM = 2;
//    private static final int MAX_MESSAGES = 100;
//    private ArrayBlockingQueue<ModuleMessage> messages = new ArrayBlockingQueue<>(MAX_MESSAGES);

    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream(args[1]));
            Long interval = Long.parseLong(prop.getProperty("computationInterval"));

            ConcurrentLinkedQueue<ModuleMessage> timerQueue = new ConcurrentLinkedQueue<>();
            ConcurrentLinkedQueue<ModuleMessage> communicationQueue = new ConcurrentLinkedQueue<>();
            ConcurrentLinkedQueue<ModuleMessage> rmiQueue = new ConcurrentLinkedQueue<>();

            List<ConcurrentLinkedQueue<ModuleMessage>> queues = new ArrayList<>();
            queues.add(timerQueue);
            queues.add(communicationQueue);
            queues.add(rmiQueue);

            MessageHandler messageHandler = new MessageHandler(queues);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(new TimerModule(messageHandler, timerQueue));

//            CloudAtlasAgent object = new CloudAtlasAgent(args[0]);
//            CloudAtlasAPI stub =
//                    (CloudAtlasAPI) UnicastRemoteObject.exportObject(object, 0);
//            Registry registry = LocateRegistry.getRegistry();
//            registry.rebind("CloudAtlasAPI", stub);
//            System.out.println("CloudAtlasAgent bound");
        } catch (Exception e) {
            System.err.println("Agent exception:");
            e.printStackTrace();
        }
    }
}
