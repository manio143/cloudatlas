package pl.edu.mimuw.cloudatlas.agent.gossipStrategies;

public class RRVFGossipStrategy extends GossipStrategy {
    private int currentLevel;
    private int exponentialCounter;

    private final double EXPONENT = 1.42;

    protected RRVFGossipStrategy(int levels) {
        this.levels = levels;
        currentLevel = 1;
        exponentialCounter = (int)Math.pow(levels, EXPONENT);
    }

    public int nextLevel() {
        if(exponentialCounter == 0) {
            if (currentLevel == levels) {
                currentLevel = 1;
            } else {
                currentLevel++;
            }
            exponentialCounter = (int)Math.pow(levels - currentLevel + 1, EXPONENT);
        }
        exponentialCounter--;
        return currentLevel;
    }
}
