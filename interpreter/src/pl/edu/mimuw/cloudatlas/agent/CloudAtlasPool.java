package pl.edu.mimuw.cloudatlas.agent;

import pl.edu.mimuw.cloudatlas.agent.agentModules.MessageHandler;
import pl.edu.mimuw.cloudatlas.agent.agentModules.Message;
import pl.edu.mimuw.cloudatlas.agent.agentModules.Tester;
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
            Long interval = Long.parseLong(prop.getProperty("computationInterval"));

            LinkedBlockingQueue<Message> timerQueue = new LinkedBlockingQueue<>();
            LinkedBlockingQueue<Message> communicationQueue = new LinkedBlockingQueue<>();
            LinkedBlockingQueue<Message> rmiQueue = new LinkedBlockingQueue<>();
            LinkedBlockingQueue<Message> testerQueue = new LinkedBlockingQueue<>();

            List<LinkedBlockingQueue<Message>> queues = new ArrayList<>();
            queues.add(timerQueue);
            queues.add(communicationQueue);
            queues.add(rmiQueue);
            queues.add(testerQueue);

            MessageHandler messageHandler = new MessageHandler(queues);

            ExecutorService timerExecutor = Executors.newSingleThreadExecutor();
            timerExecutor.execute(new Timer(messageHandler, timerQueue));

            ExecutorService testExecutor = Executors.newSingleThreadExecutor();
            testExecutor.execute(new Tester(messageHandler, testerQueue));

        } catch (IOException e) {
            System.err.println("Agent exception:");
            e.printStackTrace();
        }
    }
}
