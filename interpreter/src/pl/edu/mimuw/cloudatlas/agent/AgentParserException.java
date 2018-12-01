package pl.edu.mimuw.cloudatlas.agent;

public class AgentParserException extends AgentException {
    protected AgentParserException(String query) {
        super("Incorrect query: " + query);
    }
}