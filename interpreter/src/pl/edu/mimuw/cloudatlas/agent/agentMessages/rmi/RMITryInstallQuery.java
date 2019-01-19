package pl.edu.mimuw.cloudatlas.agent.agentMessages.rmi;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;

import static pl.edu.mimuw.cloudatlas.agent.utility.Operation.RMI_TRY_INSTALL_QUERY;

public class RMITryInstallQuery extends MessageContent {
    public RMITryInstallQuery() {
        operation = RMI_TRY_INSTALL_QUERY;
    }
}
