package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.model.Attribute;

import java.util.List;
import java.util.Map;

import static pl.edu.mimuw.cloudatlas.agent.agentMessages.MessageContent.Operation.RMI_QUERIES;

public class RMIQueries extends MessageContent {
    public Map<String, List<Attribute>> queries;

    public RMIQueries(Map<String, List<Attribute>> queries) {
        this.queries = queries;

        this.operation = RMI_QUERIES;
    }
}
