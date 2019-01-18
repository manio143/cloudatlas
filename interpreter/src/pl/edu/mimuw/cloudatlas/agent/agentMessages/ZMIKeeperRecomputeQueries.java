package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.agent.MessageContent;

import static pl.edu.mimuw.cloudatlas.agent.MessageContent.Operation.ZMI_KEEPER_RECOMPUTE_QUERIES;

public class ZMIKeeperRecomputeQueries extends MessageContent {
    public ZMIKeeperRecomputeQueries () {

        operation = ZMI_KEEPER_RECOMPUTE_QUERIES;
    }

    public ZMIKeeperRecomputeQueries copy() {
        return new ZMIKeeperRecomputeQueries();
    }
}
