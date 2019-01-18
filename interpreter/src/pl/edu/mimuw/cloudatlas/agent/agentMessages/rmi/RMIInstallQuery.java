package pl.edu.mimuw.cloudatlas.agent.agentMessages.rmi;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;

import static pl.edu.mimuw.cloudatlas.agent.utility.Operation.RMI_INSTALL_QUERY;

public class RMIInstallQuery extends MessageContent {
    public RMIInstallQuery() {
        operation = RMI_INSTALL_QUERY;
    }
}
