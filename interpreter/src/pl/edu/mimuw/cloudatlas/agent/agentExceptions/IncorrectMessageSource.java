package pl.edu.mimuw.cloudatlas.agent.agentExceptions;

import pl.edu.mimuw.cloudatlas.agent.Message.Module;

public class IncorrectMessageSource extends AgentException {
    public IncorrectMessageSource(Module expected, Module received) {
        super("Incorrect source module, expected: " + expected + ", got: " + received);
    }
}
