package pl.edu.mimuw.cloudatlas.agent;

import pl.edu.mimuw.cloudatlas.agent.agentModules.MessageHandler;
import pl.edu.mimuw.cloudatlas.agent.agentModules.ModuleMessage;
import pl.edu.mimuw.cloudatlas.agent.agentModules.TimerModule;

import java.io.*;
import java.sql.Timestamp;
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

            int [] a = {4000, 2000};

            for (int i = 0; i < a.length; i++) {

                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
                objectStream.writeLong(0);
                objectStream.writeLong(a[i]);
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                System.out.println(timestamp);
                objectStream.writeLong(timestamp.getTime());
                objectStream.writeObject(new Test());

                ModuleMessage message = new ModuleMessage(
                        ModuleMessage.Module.TIMER,
                        ModuleMessage.Module.TIMER,
                        ModuleMessage.Operation.TIMER_SCHEDULE,
                        byteStream.toByteArray());

                messageHandler.addMessage(message);

            }
    
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(new TimerModule(messageHandler, timerQueue));

        } catch (Exception e) {
            System.err.println("Agent exception:");
            e.printStackTrace();
        }
    }


    public static class Test implements Runnable, Serializable {
        @Override
        public void run() {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            System.out.println("Test " + timestamp);
        }
    }
}
