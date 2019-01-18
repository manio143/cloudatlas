package pl.edu.mimuw.cloudatlas.agent.agentMessages.zmiKeeper;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;
import pl.edu.mimuw.cloudatlas.model.ValueSet;

import static pl.edu.mimuw.cloudatlas.agent.utility.Operation.ZMI_KEEPER_FALLBACK_CONTACTS;

public class ZMIKeeperFallbackContacts extends MessageContent {
    public final ValueSet contacts;

    public ZMIKeeperFallbackContacts(ValueSet contacts) {
        this.contacts = contacts;

        operation = ZMI_KEEPER_FALLBACK_CONTACTS;
    }
}
