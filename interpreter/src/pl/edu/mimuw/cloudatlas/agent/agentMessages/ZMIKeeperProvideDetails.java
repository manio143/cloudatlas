package pl.edu.mimuw.cloudatlas.agent.agentMessages;

public class ZMIKeeperProvideDetails extends MessageContent {
    public final GossipRequestDetails msg;

    public ZMIKeeperProvideDetails(GossipRequestDetails msg){
        this.msg = msg;

        this.operation = Operation.ZMI_KEEPER_PROVIDE_DETAILS;
    }
}
