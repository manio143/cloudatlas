package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.agent.utility.Operation;

public class CopyNotImplementedException extends RuntimeException {
    public CopyNotImplementedException(Operation operation) {
        super("Copy constructor not implemented for: " + operation);
    }
}
