package pl.edu.mimuw.cloudatlas.agent.agentMessages.zmiKeeper;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;
import pl.edu.mimuw.cloudatlas.agent.utility.Operation;

public class ZMIKeeperFallbackContactsGossip extends MessageContent {
    public ZMIKeeperFallbackContactsGossip() {
        operation = Operation.ZMI_KEEPER_FALLBACK_CONTACTS_GOSSIP;
    }
}
