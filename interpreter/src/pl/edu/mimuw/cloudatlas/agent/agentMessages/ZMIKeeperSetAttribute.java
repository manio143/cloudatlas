package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import static pl.edu.mimuw.cloudatlas.agent.agentMessages.MessageContent.Operation.ZMI_KEEPER_SET_ATTRIBUTE;

public class ZMIKeeperSetAttribute extends MessageContent {

    public ZMIKeeperSetAttribute() {
        operation = ZMI_KEEPER_SET_ATTRIBUTE;
    }
}
