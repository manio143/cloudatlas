package pl.edu.mimuw.cloudatlas.agent.agentExceptions;

public class RestrictedAttributeException extends AgentException {
    public RestrictedAttributeException(String attribute) {
        super("Restricted name of the attribute: " + attribute);
    }
}
