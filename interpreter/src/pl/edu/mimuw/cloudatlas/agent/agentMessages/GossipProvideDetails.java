package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.agent.MessageContent;
import pl.edu.mimuw.cloudatlas.interpreter.query.Absyn.Program;
import pl.edu.mimuw.cloudatlas.model.Attribute;
import pl.edu.mimuw.cloudatlas.model.AttributesMap;
import pl.edu.mimuw.cloudatlas.model.PathName;

import java.util.Map;

public class GossipProvideDetails extends TimedGossipMessage {
    public final GossipRequestDetails sourceMsg;
    public final Map<PathName, AttributesMap> details;
    public final Map<String, Program> installedQueries;

    public GossipProvideDetails(GossipRequestDetails sourceMsg, Map<PathName, AttributesMap> details, Map<String, Program> installedQueries)
    {
        this.details = details;
        this.sourceMsg = sourceMsg;
        this.installedQueries = installedQueries;

        this.operation = Operation.GOSSIP_PROVIDE_DETAILS;
    }
}
