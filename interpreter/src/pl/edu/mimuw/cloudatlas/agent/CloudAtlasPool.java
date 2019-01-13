package pl.edu.mimuw.cloudatlas.agent;

import pl.edu.mimuw.cloudatlas.agent.agentMessages.MessageHandler;
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

            QueueKeeper keeper = new QueueKeeper();

            MessageHandler messageHandler = new MessageHandler(keeper);

            ExecutorService timer = Executors.newSingleThreadExecutor();
            timer.execute(new Timer(messageHandler, keeper.timerQueue));

            ExecutorService tester = Executors.newSingleThreadExecutor();
            tester.execute(new Tester(messageHandler, keeper.testerQueue));

            ExecutorService communication = Executors.newSingleThreadExecutor();
            communication.execute(new Communication(messageHandler, keeper.communicationQueue));

            ExecutorService rmi = Executors.newSingleThreadExecutor();
            rmi.execute(new RMI(messageHandler, keeper.rmiQueue));

            ExecutorService zmiKeeper = Executors.newSingleThreadExecutor();
            zmiKeeper.execute(new ZMIKeeper(messageHandler, keeper.zmiKeeperQueue));

        } catch (IOException e) {
            System.err.println("Agent exception:");
            e.printStackTrace();
        }
    }
}
