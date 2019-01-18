package pl.edu.mimuw.cloudatlas.agent.agentExceptions;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent.Operation;

public class IncorrectMessageContent extends AgentException {
    public IncorrectMessageContent(Operation expected, Operation received) {
        super("Incorrect content type, expected: " + expected + ", got: " + received);
    }
}
