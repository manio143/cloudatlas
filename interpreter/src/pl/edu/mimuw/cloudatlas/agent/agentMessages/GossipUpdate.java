package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.model.AttributesMap;
import pl.edu.mimuw.cloudatlas.model.PathName;

import java.util.Map;

public class GossipUpdate extends MessageContent {
    public final Map<PathName, AttributesMap> details;

    public GossipUpdate(Map<PathName, AttributesMap> details)
    {
        this.details = details;

        this.operation = Operation.GOSSIP_UPDATE;
    }
}
