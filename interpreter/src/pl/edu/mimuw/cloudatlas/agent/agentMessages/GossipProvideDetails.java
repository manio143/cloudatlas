package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.agent.MessageContent;
import pl.edu.mimuw.cloudatlas.interpreter.query.Absyn.Program;
import pl.edu.mimuw.cloudatlas.model.Attribute;
import pl.edu.mimuw.cloudatlas.model.AttributesMap;
import pl.edu.mimuw.cloudatlas.model.PathName;
import pl.edu.mimuw.cloudatlas.signer.SignedQueryRequest;

import java.util.Map;
import java.util.Set;

public class GossipProvideDetails extends TimedGossipMessage {
    public final GossipRequestDetails sourceMsg;
    public final Map<PathName, AttributesMap> details;
    public final Set<SignedQueryRequest> installedQueries;

    public GossipProvideDetails(GossipRequestDetails sourceMsg, Map<PathName, AttributesMap> details, Set<SignedQueryRequest> installedQueries)
    {
        this.details = details;
        this.sourceMsg = sourceMsg;
        this.installedQueries = installedQueries;

        this.operation = Operation.GOSSIP_PROVIDE_DETAILS;
    }
}
