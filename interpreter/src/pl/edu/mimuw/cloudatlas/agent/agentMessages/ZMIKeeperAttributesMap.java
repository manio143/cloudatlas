package pl.edu.mimuw.cloudatlas.agent.agentMessages;

public class ZMIKeeperAttributesMap extends MessageContent {
    public final String pathName;

    public ZMIKeeperAttributesMap(String pathName) {
        this.pathName = pathName;

        operation = Operation.ZMI_KEEPER_ATTRIBUTES;
    }
}
