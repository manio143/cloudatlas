package pl.edu.mimuw.cloudatlas.agent.agentMessages.zmiKeeper;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;
import pl.edu.mimuw.cloudatlas.agent.utility.Operation;
import pl.edu.mimuw.cloudatlas.model.AttributesMap;
import pl.edu.mimuw.cloudatlas.model.PathName;
import pl.edu.mimuw.cloudatlas.signer.SignedQueryRequest;

import java.util.Map;
import java.util.Set;

public class ZMIKeeperUpdateZMI extends MessageContent {
    public final Map<PathName, AttributesMap> details;
    public final Set<SignedQueryRequest> installedQueries;
    public final long delay;

    public ZMIKeeperUpdateZMI(Map<PathName, AttributesMap> details, Set<SignedQueryRequest> installedQueries, long delay)
    {
        this.details = details;
        this.installedQueries = installedQueries;
        this.delay = delay;

        this.operation = Operation.ZMI_KEEPER_UPDATE_ZMI;
    }
}
