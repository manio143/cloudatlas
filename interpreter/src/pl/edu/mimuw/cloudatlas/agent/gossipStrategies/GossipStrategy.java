package pl.edu.mimuw.cloudatlas.agent.gossipStrategies;


public abstract class GossipStrategy {
    // A blocking call that waits an appropriate amount of time
    // before returning the next level for gossiping
    public abstract int nextLevel() throws InterruptedException;
}
