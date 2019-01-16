package pl.edu.mimuw.cloudatlas.agent;

import pl.edu.mimuw.cloudatlas.agent.agentModules.*;
import pl.edu.mimuw.cloudatlas.agent.gossipStrategies.GossipStrategy;
import pl.edu.mimuw.cloudatlas.agent.gossipStrategies.RRCFGossipStrategy;
import pl.edu.mimuw.cloudatlas.model.PathName;
import pl.edu.mimuw.cloudatlas.model.ValueContact;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.concurrent.*;

public class CloudAtlasPool {
    private final CloudAtlasAgent agent;
    private final Properties properties;

    private final int MILISECONDS = 1000;

    public CloudAtlasPool(CloudAtlasAgent agent, Properties properties) {
        this.properties = properties;
        this.agent = agent;
    }

    public void runModules() throws UnknownHostException {

        long computationInterval = MILISECONDS * Long.parseLong(properties.getProperty("computationInterval"));
        long gossipFrequency = MILISECONDS * Integer.parseInt(properties.getProperty("gossipFrequency"));

        String pathName = properties.getProperty("pathName");
        String strategyName = properties.getProperty("peerSelectionStrategy");
        InetAddress ip = InetAddress.getByName(properties.getProperty("ip"));

        ValueContact thisMachine = new ValueContact(new PathName(pathName), ip);
        GossipStrategy strategy = GossipStrategy.fromName(strategyName, pathName);

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
        zmiKeeper.execute(new ZMIKeeper(messageHandler, keeper.zmiKeeperQueue, agent, computationInterval));

        ExecutorService gossip = Executors.newSingleThreadExecutor();
        gossip.execute(new Gossip(messageHandler, keeper.gossipQueue, thisMachine, strategy, gossipFrequency));
    }
}
