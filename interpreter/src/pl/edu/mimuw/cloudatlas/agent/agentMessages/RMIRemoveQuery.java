package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import static pl.edu.mimuw.cloudatlas.agent.agentMessages.MessageContent.Operation.RMI_REMOVE_QUERY;

public class RMIRemoveQuery extends MessageContent {
    public RMIRemoveQuery() {
        operation = RMI_REMOVE_QUERY;
    }
}
