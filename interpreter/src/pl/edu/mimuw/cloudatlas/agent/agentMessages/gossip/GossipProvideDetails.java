package pl.edu.mimuw.cloudatlas.agent.agentMessages.gossip;

import pl.edu.mimuw.cloudatlas.agent.agentMessages.timer.TimedGossipMessage;
import pl.edu.mimuw.cloudatlas.agent.utility.Operation;
import pl.edu.mimuw.cloudatlas.model.AttributesMap;
import pl.edu.mimuw.cloudatlas.model.PathName;
import pl.edu.mimuw.cloudatlas.signer.SignedQueryRequest;

import java.util.Map;
import java.util.Set;

public class GossipProvideDetails extends TimedGossipMessage {
    public final GossipRequestDetails sourceMsg;
    public final Map<PathName, AttributesMap> details;
    public final Set<SignedQueryRequest> installedQueries;
    public final Set<Long> uninstalledQueries;

    public GossipProvideDetails(GossipRequestDetails sourceMsg, Map<PathName, AttributesMap> details, Set<SignedQueryRequest> installedQueries, Set<Long> uninstalledQueries)
    {
        this.details = details;
        this.sourceMsg = sourceMsg;
        this.installedQueries = installedQueries;
        this.uninstalledQueries = uninstalledQueries;

        this.operation = Operation.GOSSIP_PROVIDE_DETAILS;
    }
}
