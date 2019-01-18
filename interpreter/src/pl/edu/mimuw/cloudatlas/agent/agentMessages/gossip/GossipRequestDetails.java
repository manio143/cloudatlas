package pl.edu.mimuw.cloudatlas.agent.agentMessages.gossip;

import pl.edu.mimuw.cloudatlas.agent.agentMessages.timer.TimedGossipMessage;
import pl.edu.mimuw.cloudatlas.model.PathName;

import java.net.InetAddress;
import java.util.List;

public class GossipRequestDetails extends TimedGossipMessage {
    public final List<PathName> nodes;
    public final InetAddress responseAddress;

    public GossipRequestDetails(List<PathName> nodes, InetAddress responseAddress) {
        this.nodes = nodes;
        this.responseAddress = responseAddress;

        this.operation = Operation.GOSSIP_REQUEST_DETAILS;
    }
}
