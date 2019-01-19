package pl.edu.mimuw.cloudatlas.agent.gossipStrategies;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RVPGossipStrategy extends GossipStrategy {
    private final double EXPONENT = 1.3;

    private final Random rand = new Random();
    private final List<Double> levelProbabilites = new ArrayList<>();
    private double probabilitiesSum = 0;

    protected RVPGossipStrategy(int levels) {
        this.levels = levels;
        double toAdd = 1;
        for (int i = 0; i < levels; i++) {
            probabilitiesSum += toAdd;
            levelProbabilites.add(probabilitiesSum);
            toAdd *= EXPONENT;
        }
    }

    public int nextLevel() {
        double random = rand.nextDouble() * probabilitiesSum;
        for (int i = 0; i < levels; i++) {
            if (levelProbabilites.get(i) > random) {
                return levels - i;
            }
        }
        return levels;
    }
}
