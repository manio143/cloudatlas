package pl.edu.mimuw.cloudatlas.agent.agentMessages;

public class GossipNext extends MessageContent {
    public final int level;

    public GossipNext(int level) {
        this.level = level;

        operation = Operation.GOSSIP_NEXT;
    }
}
