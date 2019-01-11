package pl.edu.mimuw.cloudatlas.agent.agentModules;

import java.io.Serializable;

public class ModuleMessage implements Serializable {
    public enum Module {
        TIMER, COMMUNICATION, RMI, TESTER
    }

    public enum Operation {
        TIMER_ADD_EVENT, TIMER_REMOVE_EVENT
    }

    public final Module src;
    public final Module dest;

    public final Operation operation;

    public final byte[] contents;

    public ModuleMessage(Module src, Module dest, Operation operation, byte[] arguments) {
        this.src = src;
        this.dest = dest;
        this.operation = operation;
        this.contents = arguments;
    }
}
