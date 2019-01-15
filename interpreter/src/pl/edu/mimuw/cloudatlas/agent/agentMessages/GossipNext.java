package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.agent.MessageContent;

public class GossipNext extends MessageContent {
    public final int level;

    public GossipNext(int level) {
        this.level = level;

        operation = Operation.GOSSIP_NEXT;
    }
}
