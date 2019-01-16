package pl.edu.mimuw.cloudatlas.agent.gossipStrategies;

public class RRVFGossipStrategy extends GossipStrategy {
    private int currentLevel;
    private int exponentialCounter;

    private final double EXPONENT = 1.3;

    protected RRVFGossipStrategy(int levels) {
        this.levels = levels;
        currentLevel = levels;
        exponentialCounter = (int)Math.pow(levels, EXPONENT);
    }

    public int nextLevel() {
        if(exponentialCounter == 0) {
            if (currentLevel == 0) {
                currentLevel = levels;
            } else {
                currentLevel--;
            }
            exponentialCounter = (int)Math.pow(currentLevel, EXPONENT);
        }
        exponentialCounter--;
        return currentLevel;
    }
}
