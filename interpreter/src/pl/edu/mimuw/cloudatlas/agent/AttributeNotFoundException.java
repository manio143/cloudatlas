package pl.edu.mimuw.cloudatlas.agent;

public class AttributeNotFoundException extends AgentException {
    protected AttributeNotFoundException() {
        super("No query associated with that attribute");
    }
}