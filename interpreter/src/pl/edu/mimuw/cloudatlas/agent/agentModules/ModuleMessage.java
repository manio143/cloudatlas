package pl.edu.mimuw.cloudatlas.agent.agentModules;

import java.io.Serializable;

public class ModuleMessage implements Serializable {
    public enum Module {
        TIMER, COMMUNICATION, RMI
    }

    public enum Operation {
        TIMER_SCHEDULE
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
