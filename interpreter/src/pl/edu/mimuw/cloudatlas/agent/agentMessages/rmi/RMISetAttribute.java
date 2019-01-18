package pl.edu.mimuw.cloudatlas.agent.agentMessages.rmi;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;

import static pl.edu.mimuw.cloudatlas.agent.utility.MessageContent.Operation.RMI_SET_ATTRIBUTE;

public class RMISetAttribute extends MessageContent {
    public RMISetAttribute() {
        operation = RMI_SET_ATTRIBUTE;
    }
}
