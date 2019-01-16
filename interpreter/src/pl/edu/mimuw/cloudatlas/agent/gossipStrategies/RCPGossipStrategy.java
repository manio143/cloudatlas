package pl.edu.mimuw.cloudatlas.agent.gossipStrategies;

import java.util.Random;

public class RCPGossipStrategy extends GossipStrategy {
    private final Random rand = new Random();

    protected RCPGossipStrategy(int levels) {
        this.levels = levels;
    }

    public int nextLevel() {
        return rand.nextInt(levels + 1);
    }
}
