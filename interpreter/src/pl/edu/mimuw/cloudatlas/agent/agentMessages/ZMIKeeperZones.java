package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.agent.MessageContent;

import static pl.edu.mimuw.cloudatlas.agent.MessageContent.Operation.ZMI_KEEPER_ZONES;

public class ZMIKeeperZones extends MessageContent {
    public ZMIKeeperZones() {
        this.operation = ZMI_KEEPER_ZONES;
    }
}
