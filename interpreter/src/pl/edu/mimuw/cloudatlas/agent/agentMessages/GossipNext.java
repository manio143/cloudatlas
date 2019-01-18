package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.agent.MessageContent;

public class GossipNext extends MessageContent {
    public GossipNext() {
        operation = Operation.GOSSIP_NEXT;
    }

    public GossipNext copy() {
        return new GossipNext();
    }
}
