package pl.edu.mimuw.cloudatlas.agent.agentMessages.zmiKeeper;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;

import static pl.edu.mimuw.cloudatlas.agent.utility.MessageContent.Operation.ZMI_KEEPER_RECOMPUTE_QUERIES;

public class ZMIKeeperRecomputeQueries extends MessageContent {
    public ZMIKeeperRecomputeQueries () {

        operation = ZMI_KEEPER_RECOMPUTE_QUERIES;
    }

    public ZMIKeeperRecomputeQueries copy() {
        return new ZMIKeeperRecomputeQueries();
    }
}
