package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.model.PathName;
import pl.edu.mimuw.cloudatlas.model.ValueTime;

import java.net.InetAddress;
import java.util.List;

public class GossipInterFreshness extends MessageContent {
    public final List<Node> nodes;
    public final InetAddress responseAddress;
    //TODO?GTP timestamps
    public static class Node {
        public final PathName pathName;
        public final ValueTime freshness;
        public Node(PathName pathName, ValueTime freshness) {
            this.freshness = freshness;
            this.pathName = pathName;
        }
    }

    private GossipInterFreshness(List<Node> nodes, InetAddress responseAddress, Operation operation) {
        this.nodes = nodes;
        this.responseAddress = responseAddress;

        this.operation = operation;
    }

    public static GossipInterFreshness Start(List<Node> nodes, InetAddress responseAddress){
        return new GossipInterFreshness(nodes, responseAddress, Operation.GOSSIP_INTER_FRESHNESS_START);
    }

    public static GossipInterFreshness Response(List<Node> nodes, InetAddress responseAddress){
        return new GossipInterFreshness(nodes, responseAddress, Operation.GOSSIP_INTER_FRESHNESS_RESPONSE);
    }
}
