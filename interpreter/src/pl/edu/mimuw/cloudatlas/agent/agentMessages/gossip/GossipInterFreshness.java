package pl.edu.mimuw.cloudatlas.agent.agentMessages.gossip;

import pl.edu.mimuw.cloudatlas.agent.agentMessages.timer.TimedGossipMessage;
import pl.edu.mimuw.cloudatlas.agent.utility.Node;
import pl.edu.mimuw.cloudatlas.agent.utility.Operation;
import pl.edu.mimuw.cloudatlas.model.ValueContact;

import java.util.List;

public class GossipInterFreshness extends TimedGossipMessage {
    public final List<Node> nodes;
    public final ValueContact responseContact;
    public final int level;
    public final int id;

    private GossipInterFreshness(List<Node> nodes, ValueContact responseContact, int level, Operation operation, int id) {
        this.nodes = nodes;
        this.responseContact = responseContact;
        this.level = level;
        this.id = id;

        this.operation = operation;
    }

    public static GossipInterFreshness Start(List<Node> nodes, ValueContact responseContact, int level, int id){
        return new GossipInterFreshness(nodes, responseContact, level, Operation.GOSSIP_INTER_FRESHNESS_START, id);
    }

    public static GossipInterFreshness Response(List<Node> nodes, ValueContact responseContact, int level, int id){
        return new GossipInterFreshness(nodes, responseContact, level, Operation.GOSSIP_INTER_FRESHNESS_RESPONSE, id);
    }
}
