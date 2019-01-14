package pl.edu.mimuw.cloudatlas.agent.agentMessages;

public class ZMIKeeperSiblings extends MessageContent {
    public final String pathName;
    public final int level;

    public ZMIKeeperSiblings(int level, String pathName) {
        this.pathName = pathName;
        this.level = level;

        operation = Operation.ZMI_KEEPER_SIBLINGS;
    }
}
