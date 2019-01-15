package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.agent.MessageContent;

public class ZMIKeeperSiblingsForGossip extends MessageContent {
    public final GossipInterFreshness msg;

    public ZMIKeeperSiblingsForGossip(GossipInterFreshness msg){
        this.msg = msg;

        this.operation = Operation.ZMI_KEEPER_SIBLINGS_FOR_GOSSIP;
    }
}
