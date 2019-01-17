package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.agent.MessageContent;
import pl.edu.mimuw.cloudatlas.model.Attribute;

import java.util.List;
import java.util.Map;

import static pl.edu.mimuw.cloudatlas.agent.MessageContent.Operation.RMI_QUERIES;

public class RMIQueries extends MessageContent {
    public Map<String, List<String>> queries;

    public RMIQueries(Map<String, List<String>> queries) {
        this.queries = queries;

        this.operation = RMI_QUERIES;
    }
}
