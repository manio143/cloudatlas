package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.agent.MessageContent;

import static pl.edu.mimuw.cloudatlas.agent.MessageContent.Operation.ZMI_KEEPER_QUERIES;

public class ZMIKeeperQueries extends MessageContent {
    public ZMIKeeperQueries() {
        operation = ZMI_KEEPER_QUERIES;
    }
}
