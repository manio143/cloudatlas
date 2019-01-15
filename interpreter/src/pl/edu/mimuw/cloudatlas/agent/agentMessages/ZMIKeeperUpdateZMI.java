package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.agent.MessageContent;
import pl.edu.mimuw.cloudatlas.model.AttributesMap;
import pl.edu.mimuw.cloudatlas.model.PathName;

import java.util.Map;

public class ZMIKeeperUpdateZMI extends MessageContent {
    public final Map<PathName, AttributesMap> details;

    public ZMIKeeperUpdateZMI(Map<PathName, AttributesMap> details)
    {
        this.details = details;

        this.operation = Operation.ZMI_KEEPER_UPDATE_ZMI;
    }
}
