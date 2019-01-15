package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.model.PathName;
import pl.edu.mimuw.cloudatlas.model.ValueTime;

import java.net.InetAddress;
import java.util.List;

public class GossipRequestDetails extends MessageContent {
    public final List<PathName> nodes;
    public final InetAddress responseAddress;

    public GossipRequestDetails(List<PathName> nodes, InetAddress responseAddress) {
        this.nodes = nodes;
        this.responseAddress = responseAddress;

        this.operation = Operation.GOSSIP_REQUEST_DETAILS;
    }
}
