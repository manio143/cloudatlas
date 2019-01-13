package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import static pl.edu.mimuw.cloudatlas.agent.agentMessages.MessageContent.Operation.ZMI_KEEPER_REMOVE_QUERY;

public class ZMIKeeperRemoveQueries extends MessageContent {

    public ZMIKeeperRemoveQueries() {
        operation = ZMI_KEEPER_REMOVE_QUERY;
    }
}
