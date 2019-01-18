package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;

public class CopyNotImplementedException extends RuntimeException {
    public CopyNotImplementedException(MessageContent.Operation operation) {
        super("Copy constructor not implemented for: " + operation);
    }
}
