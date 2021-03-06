package pl.edu.mimuw.cloudatlas.agent.agentMessages.gossip;

import java.util.List;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;
import pl.edu.mimuw.cloudatlas.agent.utility.Operation;
import pl.edu.mimuw.cloudatlas.model.ValueContact;

public class GossipContacts extends MessageContent {
    public final List<ValueContact> contacts;

    public GossipContacts(List<ValueContact> contacts) {
        this.contacts = contacts;

        operation = Operation.GOSSIP_CONTACTS;
    }
}
