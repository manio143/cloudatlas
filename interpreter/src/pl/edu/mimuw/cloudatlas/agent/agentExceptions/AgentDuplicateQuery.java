package pl.edu.mimuw.cloudatlas.agent.agentExceptions;

public class AgentDuplicateQuery extends AgentException {
    public AgentDuplicateQuery(String query) {
        super("Duplicate query: " + query);
    }
}
