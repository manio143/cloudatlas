package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.agent.MessageContent;
import pl.edu.mimuw.cloudatlas.model.Value;

import static pl.edu.mimuw.cloudatlas.agent.MessageContent.Operation.ZMI_KEEPER_SET_ATTRIBUTE;

public class ZMIKeeperSetAttribute extends MessageContent {
    public final String pathName;
    public final String attribute;
    public final Value value;

    public ZMIKeeperSetAttribute(String pathName, String attribute, Value value) {
        this.pathName = pathName;
        this.attribute = attribute;
        this.value = value;

        operation = ZMI_KEEPER_SET_ATTRIBUTE;
    }
}
