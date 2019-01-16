package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.agent.MessageContent;
import pl.edu.mimuw.cloudatlas.model.AttributesMap;
import pl.edu.mimuw.cloudatlas.model.PathName;

import java.util.Map;

public class GossipProvideDetails extends TimedGossipMessage {
    public final GossipRequestDetails sourceMsg;
    public final Map<PathName, AttributesMap> details;

    public GossipProvideDetails(GossipRequestDetails sourceMsg, Map<PathName, AttributesMap> details)
    {
        this.details = details;
        this.sourceMsg = sourceMsg;

        this.operation = Operation.GOSSIP_PROVIDE_DETAILS;
    }
}
