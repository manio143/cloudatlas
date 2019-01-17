package pl.edu.mimuw.cloudatlas.agent.gossipStrategies;

import pl.edu.mimuw.cloudatlas.model.PathName;

public abstract class GossipStrategy {
    protected int levels;

    public static GossipStrategy fromName(String name, String nodePath) {
        int levels = 1;
        try {
            levels = new PathName(nodePath).getComponents().size();
        } catch (Exception e) {
            System.out.println("Cannot calculate levels for GossipStrategy!");
            e.printStackTrace();
        }

        switch (name) {

            case "RoundRobinConstantFrequency":
                return new RRCFGossipStrategy(levels);
            case "RoundRobinVariableFrequency":
                return new RRVFGossipStrategy(levels);
            case "RandomConstantProbability":
                return new RCPGossipStrategy(levels);
            case "RandomVariableProbability":
                return new RVPGossipStrategy(levels);
            default:
                System.out.println("Not a correct strategy name: " + name);
        }

        GossipStrategy defaultStrategy = new RRCFGossipStrategy(levels); //TODO choose at random

        return defaultStrategy;
    }

    public abstract int nextLevel() throws InterruptedException;
}
