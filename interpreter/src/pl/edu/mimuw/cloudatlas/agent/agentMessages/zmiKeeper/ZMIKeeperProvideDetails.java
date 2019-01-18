package pl.edu.mimuw.cloudatlas.agent.agentMessages.zmiKeeper;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.gossip.GossipRequestDetails;
import pl.edu.mimuw.cloudatlas.agent.utility.Operation;

public class ZMIKeeperProvideDetails extends MessageContent {
    public final GossipRequestDetails msg;

    public ZMIKeeperProvideDetails(GossipRequestDetails msg){
        this.msg = msg;

        this.operation = Operation.ZMI_KEEPER_PROVIDE_DETAILS;
    }
}
