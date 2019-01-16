package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.agent.MessageContent;
import pl.edu.mimuw.cloudatlas.model.ValueContact;

public class ZMIKeeperFreshnessForContact extends MessageContent {
    public final ValueContact contact;

    public ZMIKeeperFreshnessForContact(ValueContact contact) {
        this.contact = contact;

        this.operation = Operation.ZMI_KEEPER_FRESHNESS_FOR_CONTACT;
    }
}
