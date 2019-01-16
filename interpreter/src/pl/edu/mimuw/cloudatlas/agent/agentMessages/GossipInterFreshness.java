package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.agent.MessageContent;
import pl.edu.mimuw.cloudatlas.model.PathName;
import pl.edu.mimuw.cloudatlas.model.ValueContact;
import pl.edu.mimuw.cloudatlas.model.ValueTime;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.List;

public class GossipInterFreshness extends TimedGossipMessage {
    public final List<Node> nodes;
    public final ValueContact responseContact;
    public final int level;
    //TODO?GTP timestamps
    
    public static class Node implements Serializable {
        public final PathName pathName;
        public final ValueTime freshness;
        public Node(PathName pathName, ValueTime freshness) {
            this.freshness = freshness;
            this.pathName = pathName;
        }
    }

    private GossipInterFreshness(List<Node> nodes, ValueContact responseContact, int level, Operation operation) {
        this.nodes = nodes;
        this.responseContact = responseContact;
        this.level = level;

        this.operation = operation;
    }

    public static GossipInterFreshness Start(List<Node> nodes, ValueContact responseContact, int level){
        return new GossipInterFreshness(nodes, responseContact, level, Operation.GOSSIP_INTER_FRESHNESS_START);
    }

    public static GossipInterFreshness Response(List<Node> nodes, ValueContact responseContact, int level){
        return new GossipInterFreshness(nodes, responseContact, level, Operation.GOSSIP_INTER_FRESHNESS_RESPONSE);
    }
}
