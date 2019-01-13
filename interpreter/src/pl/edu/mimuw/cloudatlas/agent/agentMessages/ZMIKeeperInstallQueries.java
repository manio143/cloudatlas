package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.signer.SignedQueryRequest;

import static pl.edu.mimuw.cloudatlas.agent.agentMessages.MessageContent.Operation.ZMI_KEEPER_INSTALL_QUERY;

public class ZMIKeeperInstallQueries extends MessageContent {
    public final SignedQueryRequest query;

    public ZMIKeeperInstallQueries(SignedQueryRequest query) {
        this.query = query;

        operation = ZMI_KEEPER_INSTALL_QUERY;
    }
}
