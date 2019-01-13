package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import static pl.edu.mimuw.cloudatlas.agent.agentMessages.MessageContent.Operation.RMI_FALLBACK_CONTACTS;

public class RMIFallbackContacts extends MessageContent {
    public RMIFallbackContacts() {
        operation = RMI_FALLBACK_CONTACTS;
    }
}
