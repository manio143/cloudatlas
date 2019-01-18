package pl.edu.mimuw.cloudatlas.agent.agentMessages.gossip;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;

import java.util.List;

public class GossipSiblingsFreshness extends MessageContent {
    public final GossipInterFreshness sourceMsg;
    public final List<GossipInterFreshness.Node> localData;

    public GossipSiblingsFreshness(GossipInterFreshness sourceMsg, List<GossipInterFreshness.Node> localData)
    {
        this.localData = localData;
        this.sourceMsg = sourceMsg;

        this.operation = Operation.GOSSIP_SIBLINGS_FRESHNESS;
    }
}
