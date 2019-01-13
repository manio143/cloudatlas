package pl.edu.mimuw.cloudatlas.agent;

import pl.edu.mimuw.cloudatlas.agent.agentMessages.Message;
import pl.edu.mimuw.cloudatlas.agent.agentModules.*;
import pl.edu.mimuw.cloudatlas.agent.agentModules.Timer;

import java.io.*;
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
            Long computationInterval = Long.parseLong(prop.getProperty("computationInterval"));
            String pathName = prop.getProperty("pathName");

            LinkedBlockingQueue<Message> timerQueue = new LinkedBlockingQueue<Message>();
            LinkedBlockingQueue<Message> communicationQueue = new LinkedBlockingQueue<>();
            LinkedBlockingQueue<Message> rmiQueue = new LinkedBlockingQueue<>();
            LinkedBlockingQueue<Message> testerQueue = new LinkedBlockingQueue<>();

            List<LinkedBlockingQueue<Message>> queues = new ArrayList<>();
            queues.add(timerQueue);
            queues.add(communicationQueue);
            queues.add(rmiQueue);
            queues.add(testerQueue);

            MessageHandler messageHandler = new MessageHandler(queues);

            ExecutorService timer = Executors.newSingleThreadExecutor();
            timer.execute(new Timer(messageHandler, timerQueue));

            ExecutorService tester = Executors.newSingleThreadExecutor();
            tester.execute(new Tester(messageHandler, testerQueue));

            ExecutorService communication = Executors.newSingleThreadExecutor();
            communication.execute(new Communication(messageHandler, communicationQueue));

        } catch (IOException e) {
            System.err.println("Agent exception:");
            e.printStackTrace();
        }
    }
}
