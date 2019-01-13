package pl.edu.mimuw.cloudatlas.agent.agentExceptions;

public class QueryNotFoundException extends AgentException {
    public QueryNotFoundException(String attribute) {
        super("No query associated with name: " + attribute);
    }
}