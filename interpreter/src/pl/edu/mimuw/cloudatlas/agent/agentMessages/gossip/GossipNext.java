package pl.edu.mimuw.cloudatlas.agent.agentMessages.gossip;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;
import pl.edu.mimuw.cloudatlas.agent.utility.Operation;

public class GossipNext extends MessageContent {
    public GossipNext() {
        operation = Operation.GOSSIP_NEXT;
    }

    public GossipNext copy() {
        return new GossipNext();
    }
}
