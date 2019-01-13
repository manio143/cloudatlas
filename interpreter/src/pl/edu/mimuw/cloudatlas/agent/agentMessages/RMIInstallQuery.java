package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import static pl.edu.mimuw.cloudatlas.agent.agentMessages.MessageContent.Operation.RMI_INSTALL_QUERY;

public class RMIInstallQuery extends MessageContent {
    public RMIInstallQuery() {
        operation = RMI_INSTALL_QUERY;
    }
}
