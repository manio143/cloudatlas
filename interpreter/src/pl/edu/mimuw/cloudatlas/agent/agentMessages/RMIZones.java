package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import java.util.List;

import static pl.edu.mimuw.cloudatlas.agent.agentMessages.MessageContent.Operation.RMI_ZONES;

public class RMIZones extends MessageContent {
    public List<String> zones;

    public RMIZones(List<String> zones) {
        this.zones = zones;

        operation = RMI_ZONES;
    }
}
