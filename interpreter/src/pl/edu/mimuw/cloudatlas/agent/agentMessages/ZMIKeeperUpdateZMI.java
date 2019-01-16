package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.agent.MessageContent;
import pl.edu.mimuw.cloudatlas.interpreter.query.Absyn.Program;
import pl.edu.mimuw.cloudatlas.model.AttributesMap;
import pl.edu.mimuw.cloudatlas.model.PathName;

import java.util.Map;

public class ZMIKeeperUpdateZMI extends MessageContent {
    public final Map<PathName, AttributesMap> details;
    public final Map<String, Program> installedQueries;
    public final long delay;

    public ZMIKeeperUpdateZMI(Map<PathName, AttributesMap> details, Map<String, Program> installedQueries, long delay)
    {
        this.details = details;
        this.installedQueries = installedQueries;
        this.delay = delay;

        this.operation = Operation.ZMI_KEEPER_UPDATE_ZMI;
    }
}
