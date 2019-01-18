package pl.edu.mimuw.cloudatlas.agent.agentMessages.zmiKeeper;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;

import static pl.edu.mimuw.cloudatlas.agent.utility.MessageContent.Operation.ZMI_KEEPER_ZONES;

public class ZMIKeeperZones extends MessageContent {
    public ZMIKeeperZones() {
        this.operation = ZMI_KEEPER_ZONES;
    }
}
