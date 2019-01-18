package pl.edu.mimuw.cloudatlas.agent.agentMessages.zmiKeeper;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;
import pl.edu.mimuw.cloudatlas.agent.utility.Operation;

public class ZMIKeeperCleanup extends MessageContent {
    public ZMIKeeperCleanup() {
        this.operation = Operation.ZMI_KEEPER_CLEANUP;
    }

    public ZMIKeeperCleanup copy() {
        return new ZMIKeeperCleanup();
    }
}
