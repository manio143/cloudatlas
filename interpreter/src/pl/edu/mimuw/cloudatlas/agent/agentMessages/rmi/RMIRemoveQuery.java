package pl.edu.mimuw.cloudatlas.agent.agentMessages.rmi;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;

import static pl.edu.mimuw.cloudatlas.agent.utility.MessageContent.Operation.RMI_REMOVE_QUERY;

public class RMIRemoveQuery extends MessageContent {
    public RMIRemoveQuery() {
        operation = RMI_REMOVE_QUERY;
    }
}
