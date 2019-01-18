package pl.edu.mimuw.cloudatlas.agent.agentMessages.rmi;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;

import static pl.edu.mimuw.cloudatlas.agent.utility.Operation.RMI_FALLBACK_CONTACTS;

public class RMIFallbackContacts extends MessageContent {
    public RMIFallbackContacts() {
        operation = RMI_FALLBACK_CONTACTS;
    }
}
