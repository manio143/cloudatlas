package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.agent.MessageContent;

public class ZMIKeeperCleanup extends MessageContent {
    public ZMIKeeperCleanup() {
        this.operation = Operation.ZMI_KEEPER_CLEANUP;
    }

    public ZMIKeeperCleanup copy() {
        return new ZMIKeeperCleanup();
    }
}
