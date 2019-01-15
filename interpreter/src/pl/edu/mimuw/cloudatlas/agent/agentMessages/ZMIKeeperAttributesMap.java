package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.agent.MessageContent;

public class ZMIKeeperAttributesMap extends MessageContent {
    public final String pathName;

    public ZMIKeeperAttributesMap(String pathName) {
        this.pathName = pathName;

        operation = Operation.ZMI_KEEPER_ATTRIBUTES;
    }
}
