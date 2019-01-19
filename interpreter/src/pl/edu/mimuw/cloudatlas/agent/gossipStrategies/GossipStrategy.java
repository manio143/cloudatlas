package pl.edu.mimuw.cloudatlas.agent.gossipStrategies;

import pl.edu.mimuw.cloudatlas.agent.utility.Logger;
import pl.edu.mimuw.cloudatlas.model.PathName;

import java.util.Random;

public abstract class GossipStrategy {
    private static final int STRATEGIES_COUNT = 4;

    protected int levels;

    protected static Logger logger = new Logger("GOSSIP_STRATEGY");

    private static GossipStrategy randomStrategy(int levels) {
        logger.log("Choosing strategy at random");

        Random rand = new Random();
        int random = rand.nextInt(STRATEGIES_COUNT);

        GossipStrategy strategy = new RRCFGossipStrategy(levels);;

        switch (random) {
            case 0:
                strategy = new RRCFGossipStrategy(levels);
                break;
            case 1:
                strategy = new RRVFGossipStrategy(levels);
                break;
            case 2:
                strategy = new RCPGossipStrategy(levels);
                break;
            case 3:
                strategy = new RVPGossipStrategy(levels);
                break;
            default:
        }

        return strategy;
    }

    public static GossipStrategy fromName(String name, String nodePath) {
        int levels = 1;
        try {
            levels = new PathName(nodePath).getComponents().size();
        } catch (Exception e) {
            logger.errLog("Cannot calculate levels for GossipStrategy!");
            e.printStackTrace();
        }

        GossipStrategy strategy;

        switch (name) {

            case "RoundRobinConstantFrequency":
                strategy = new RRCFGossipStrategy(levels);
                break;
            case "RoundRobinVariableFrequency":
                strategy = new RRVFGossipStrategy(levels);
                break;
            case "RandomConstantProbability":
                strategy = new RCPGossipStrategy(levels);
                break;
            case "RandomVariableProbability":
                strategy = new RVPGossipStrategy(levels);
                break;
            case "":
                strategy = randomStrategy(levels);
                break;
            default:
                logger.errLog("Not a correct strategy name: " + name);
                strategy = randomStrategy(levels);
        }

        return strategy;
    }

    public abstract int nextLevel() throws InterruptedException;
}
