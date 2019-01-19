package pl.edu.mimuw.cloudatlas.agent.agentMessages.zmiKeeper;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;
import pl.edu.mimuw.cloudatlas.signer.SignedQueryRequest;

import static pl.edu.mimuw.cloudatlas.agent.utility.Operation.ZMI_KEEPER_TRY_INSTALL_QUERY;

public class ZMIKeeperTryInstallQuery extends MessageContent {
    public final SignedQueryRequest query;

    public ZMIKeeperTryInstallQuery(SignedQueryRequest query) {
        this.query = query;

        operation = ZMI_KEEPER_TRY_INSTALL_QUERY;
    }
}
