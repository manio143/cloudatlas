package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.agent.MessageContent;
import pl.edu.mimuw.cloudatlas.model.ValueContact;

import java.util.List;

public class GossipFreshnessToSend extends MessageContent {
    public final ValueContact contact;
    public final List<GossipInterFreshness.Node> nodes;

    public GossipFreshnessToSend(ValueContact contact, List<GossipInterFreshness.Node> nodes) {
        this.contact = contact;
        this.nodes = nodes;

        this.operation = Operation.GOSSIP_FRESHNESS_TO_SEND;
    }
}
