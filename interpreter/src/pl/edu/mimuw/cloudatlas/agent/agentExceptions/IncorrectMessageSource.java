package pl.edu.mimuw.cloudatlas.agent.agentExceptions;

import pl.edu.mimuw.cloudatlas.agent.utility.ModuleName;

public class IncorrectMessageSource extends AgentException {
    public IncorrectMessageSource(ModuleName expected, ModuleName received) {
        super("Incorrect source module, expected: " + expected + ", got: " + received);
    }
}
