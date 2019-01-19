package pl.edu.mimuw.cloudatlas.agent.agentMessages.gossip;

import pl.edu.mimuw.cloudatlas.agent.agentMessages.timer.TimedGossipMessage;
import pl.edu.mimuw.cloudatlas.agent.utility.Operation;
import pl.edu.mimuw.cloudatlas.model.AttributesMap;
import pl.edu.mimuw.cloudatlas.model.PathName;
import pl.edu.mimuw.cloudatlas.model.ValueContact;
import pl.edu.mimuw.cloudatlas.signer.SignedQueryRequest;

import java.util.Map;
import java.util.Set;

public class GossipUpdate extends TimedGossipMessage {
    public final Map<PathName, AttributesMap> details;
    public final Set<SignedQueryRequest> installedQueries;
    public final Set<Long> uninstalledQueries;
    public final ValueContact responseContact;

    public GossipUpdate(Map<PathName, AttributesMap> details, Set<SignedQueryRequest> installedQueries, Set<Long> uninstalledQueries, ValueContact responseContact)
    {
        this.details = details;
        this.installedQueries = installedQueries;
        this.uninstalledQueries = uninstalledQueries;
        this.responseContact = responseContact;

        this.operation = Operation.GOSSIP_UPDATE;
    }
}
