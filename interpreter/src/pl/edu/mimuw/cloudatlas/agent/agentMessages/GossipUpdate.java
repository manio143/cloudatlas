package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.agent.MessageContent;
import pl.edu.mimuw.cloudatlas.interpreter.query.Absyn.Program;
import pl.edu.mimuw.cloudatlas.model.AttributesMap;
import pl.edu.mimuw.cloudatlas.model.PathName;
import pl.edu.mimuw.cloudatlas.model.ValueContact;

import java.util.Map;

public class GossipUpdate extends TimedGossipMessage {
    public final Map<PathName, AttributesMap> details;
    public final Map<String, Program> installedQueries;
    public final ValueContact responseContact;

    public GossipUpdate(Map<PathName, AttributesMap> details, Map<String, Program> installedQueries, ValueContact responseContact)
    {
        this.details = details;
        this.installedQueries = installedQueries;
        this.responseContact = responseContact;

        this.operation = Operation.GOSSIP_UPDATE;
    }
}
