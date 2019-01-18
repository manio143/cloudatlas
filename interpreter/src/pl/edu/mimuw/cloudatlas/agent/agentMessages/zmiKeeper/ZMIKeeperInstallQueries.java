package pl.edu.mimuw.cloudatlas.agent.agentMessages.zmiKeeper;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;
import pl.edu.mimuw.cloudatlas.signer.SignedQueryRequest;

import static pl.edu.mimuw.cloudatlas.agent.utility.MessageContent.Operation.ZMI_KEEPER_INSTALL_QUERY;

public class ZMIKeeperInstallQueries extends MessageContent {
    public final SignedQueryRequest query;

    public ZMIKeeperInstallQueries(SignedQueryRequest query) {
        this.query = query;

        operation = ZMI_KEEPER_INSTALL_QUERY;
    }
}
