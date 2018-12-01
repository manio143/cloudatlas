package pl.edu.mimuw.cloudatlas.agent;

public class QueryNotFoundException extends AgentException {
    protected QueryNotFoundException(String attribute) {
        super("No query associated with name: " + attribute);
    }
}