package pl.edu.mimuw.cloudatlas.agent.agentMessages.gossip;

import java.util.List;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;
import pl.edu.mimuw.cloudatlas.agent.utility.Operation;
import pl.edu.mimuw.cloudatlas.agent.utility.Sibling;

public class GossipSiblings extends MessageContent {
    public final List<Sibling> siblings;

    public GossipSiblings(List<Sibling> siblings) {
        this.siblings = siblings;

        operation = Operation.GOSSIP_SIBLINGS;
    }
}
