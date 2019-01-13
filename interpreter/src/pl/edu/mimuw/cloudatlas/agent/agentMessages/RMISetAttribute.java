package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import static pl.edu.mimuw.cloudatlas.agent.agentMessages.MessageContent.Operation.RMI_SET_ATTRIBUTE;

public class RMISetAttribute extends MessageContent {
    public RMISetAttribute() {
        operation = RMI_SET_ATTRIBUTE;
    }
}
