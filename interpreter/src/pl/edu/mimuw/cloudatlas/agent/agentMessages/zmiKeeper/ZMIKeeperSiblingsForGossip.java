package pl.edu.mimuw.cloudatlas.agent.agentMessages.zmiKeeper;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.gossip.GossipInterFreshness;
import pl.edu.mimuw.cloudatlas.agent.utility.Operation;

public class ZMIKeeperSiblingsForGossip extends MessageContent {
    public final GossipInterFreshness msg;

    public ZMIKeeperSiblingsForGossip(GossipInterFreshness msg){
        this.msg = msg;

        this.operation = Operation.ZMI_KEEPER_SIBLINGS_FOR_GOSSIP;
    }
}
