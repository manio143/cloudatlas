package pl.edu.mimuw.cloudatlas.agent.agentMessages.zmiKeeper;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;
import pl.edu.mimuw.cloudatlas.signer.SignedQueryRequest;

import static pl.edu.mimuw.cloudatlas.agent.utility.Operation.ZMI_KEEPER_REMOVE_QUERY;

public class ZMIKeeperRemoveQueries extends MessageContent {
    public final SignedQueryRequest query;

    public ZMIKeeperRemoveQueries(SignedQueryRequest query) {
        this.query = query;

        operation = ZMI_KEEPER_REMOVE_QUERY;
    }
}
