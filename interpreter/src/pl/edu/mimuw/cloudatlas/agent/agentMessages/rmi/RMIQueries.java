package pl.edu.mimuw.cloudatlas.agent.agentMessages.rmi;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;

import java.util.List;
import java.util.Map;

import static pl.edu.mimuw.cloudatlas.agent.utility.MessageContent.Operation.RMI_QUERIES;

public class RMIQueries extends MessageContent {
    public Map<String, List<String>> queries;

    public RMIQueries(Map<String, List<String>> queries) {
        this.queries = queries;

        this.operation = RMI_QUERIES;
    }
}
