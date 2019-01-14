package pl.edu.mimuw.cloudatlas.agent.agentExceptions;

import pl.edu.mimuw.cloudatlas.agent.agentMessages.Message.Module;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.MessageContent.Operation;

public class IncorrectMessageSource extends AgentException {
    public IncorrectMessageSource(Module expected, Module received) {
        super("Incorrect source module, expected: " + expected + ", got: " + received);
    }
}
