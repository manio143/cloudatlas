package pl.edu.mimuw.cloudatlas.agent.gossipStrategies;

import java.util.*;
// Round Robin Constant Frequency
public class RRCFGossipStrategy extends GossipStrategy {
    private int currentLevel;

    protected RRCFGossipStrategy(int levels) {
        this.levels = levels;
        currentLevel = levels;
    }

    public int nextLevel() {
        if (currentLevel == -1) {
            currentLevel = levels;
        }
        return currentLevel--;
    }
}