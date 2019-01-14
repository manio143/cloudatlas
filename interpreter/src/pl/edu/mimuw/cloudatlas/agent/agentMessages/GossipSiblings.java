package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import java.util.Map;
import java.util.List;

import pl.edu.mimuw.cloudatlas.model.PathName;
import pl.edu.mimuw.cloudatlas.model.ValueContact;

public class GossipSiblings extends MessageContent {
    public final Map<PathName, List<ValueContact>> contacts;

    public GossipSiblings(Map<PathName, List<ValueContact>> contacts) {
        this.contacts = contacts;

        operation = Operation.GOSSIP_SIBLINGS;
    }
}
