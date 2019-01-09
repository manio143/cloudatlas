package pl.edu.mimuw.cloudatlas.agent.agentExceptions;

public class AgentException extends RuntimeException {
    protected AgentException(String message) {
        super(message);
    }
}