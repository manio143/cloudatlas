package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.agent.MessageContent;

import static pl.edu.mimuw.cloudatlas.agent.MessageContent.Operation.RMI_SET_ATTRIBUTE;

public class RMISetAttribute extends MessageContent {
    public RMISetAttribute() {
        operation = RMI_SET_ATTRIBUTE;
    }
}
