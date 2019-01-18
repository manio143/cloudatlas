package pl.edu.mimuw.cloudatlas.agent.agentMessages.zmiKeeper;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;

import static pl.edu.mimuw.cloudatlas.agent.utility.MessageContent.Operation.ZMI_KEEPER_QUERIES;

public class ZMIKeeperQueries extends MessageContent {
    public ZMIKeeperQueries() {
        operation = ZMI_KEEPER_QUERIES;
    }
}
