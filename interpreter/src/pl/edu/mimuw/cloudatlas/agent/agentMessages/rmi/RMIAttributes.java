package pl.edu.mimuw.cloudatlas.agent.agentMessages.rmi;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;
import pl.edu.mimuw.cloudatlas.model.AttributesMap;

import static pl.edu.mimuw.cloudatlas.agent.utility.MessageContent.Operation.RMI_ATTRIBUTES;

public class RMIAttributes extends MessageContent {
    public AttributesMap attributesMap;

    public RMIAttributes(AttributesMap attributesMap) {
        this.attributesMap = attributesMap;

        this.operation = RMI_ATTRIBUTES;
    }
}
