package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.model.ValueSet;

import static pl.edu.mimuw.cloudatlas.agent.agentMessages.MessageContent.Operation;

public class ZMIKeeperFallbackContactsGossip extends MessageContent {
    public ZMIKeeperFallbackContactsGossip() {
        operation = Operation.ZMI_KEEPER_FALLBACK_CONTACTS_GOSSIP;
    }
}
