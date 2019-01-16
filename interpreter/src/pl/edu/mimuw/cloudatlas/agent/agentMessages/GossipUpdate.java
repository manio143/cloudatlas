package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.agent.MessageContent;
import pl.edu.mimuw.cloudatlas.model.AttributesMap;
import pl.edu.mimuw.cloudatlas.model.PathName;
import pl.edu.mimuw.cloudatlas.model.ValueContact;

import java.util.Map;

public class GossipUpdate extends TimedGossipMessage {
    public final Map<PathName, AttributesMap> details;
    public final ValueContact responseContact;

    public GossipUpdate(Map<PathName, AttributesMap> details, ValueContact responseContact)
    {
        this.details = details;
        this.responseContact = responseContact;

        this.operation = Operation.GOSSIP_UPDATE;
    }
}
