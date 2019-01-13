package pl.edu.mimuw.cloudatlas.agent.agentExceptions;

public class AgentParserException extends AgentException {
    public AgentParserException(String query) {
        super("Incorrect query: " + query);
    }
}