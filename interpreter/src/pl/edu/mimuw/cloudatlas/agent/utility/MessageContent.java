package pl.edu.mimuw.cloudatlas.agent.utility;

import pl.edu.mimuw.cloudatlas.agent.agentMessages.CopyNotImplementedException;

import java.io.Serializable;

public abstract class MessageContent implements Serializable {

    public Operation operation;

    public MessageContent() {

    }

    public MessageContent copy() {
        throw new CopyNotImplementedException(operation);
    }

    public boolean isTimed() { return false; }
}
