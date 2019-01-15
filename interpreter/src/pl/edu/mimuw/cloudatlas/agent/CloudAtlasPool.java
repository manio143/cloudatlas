package pl.edu.mimuw.cloudatlas.agent;

import pl.edu.mimuw.cloudatlas.agent.agentMessages.GossipStrategyNext;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.MessageHandler;
import pl.edu.mimuw.cloudatlas.agent.agentModules.*;
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

    public CloudAtlasPool(CloudAtlasAgent agent, Properties properties) {
        this.properties = properties;
        this.agent = agent;
    }

    public void runModules() throws UnknownHostException {
        Long computationInterval = Long.parseLong(properties.getProperty("computationInterval"));
        String pathName = properties.getProperty("pathName");

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
        zmiKeeper.execute(new ZMIKeeper(messageHandler, keeper.zmiKeeperQueue, agent));

        InetAddress ip = InetAddress.getByName(properties.getProperty("ip"));
        ValueContact thisMachine = new ValueContact(new PathName(pathName), ip);

        ExecutorService gossip = Executors.newSingleThreadExecutor();
        gossip.execute(new Gossip(messageHandler, keeper.gossipQueue, thisMachine));

        int gossipFrequency = Integer.parseInt(properties.getProperty("gossipFrequency"));
        String strategy = properties.getProperty("peerSelectionStrategy"); //TODO implement other strategies

        ExecutorService gossipStrategy = Executors.newSingleThreadExecutor();
        gossipStrategy.execute(new GossipStrategyProvider(messageHandler, keeper.gossipStrategyQueue, new RRCFGossipStrategy(pathName, gossipFrequency)));
    }
}
