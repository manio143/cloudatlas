package pl.edu.mimuw.cloudatlas.agent.gossipStrategies;

import java.util.*;
// Round Robin Constant Frequency
public class RRCFGossipStrategy extends GossipStrategy {
    private int currentLevel;

    protected RRCFGossipStrategy(int levels) {
        this.levels = levels;
        currentLevel = 0;
    }

    public int nextLevel() {
        if (currentLevel == levels) {
            currentLevel = 0;
        }

        currentLevel++;

        return currentLevel;
    }
}