package pl.edu.mimuw.cloudatlas.agent.agentModules;

import java.io.Serializable;

public class Message implements Serializable {
    public enum Module {
        TIMER, COMMUNICATION, RMI, TESTER
    }

    public enum Operation {
        TIMER_ADD_EVENT, TIMER_REMOVE_EVENT, TIMER_ADD_EVENT_ACK, TIMER_REMOVE_EVENT_ACK
    }

    public final Module src;
    public final Module dest;

    public final Operation operation;

    public final byte[] contents;

    public Message(Module src, Module dest, Operation operation, byte[] arguments) {
        this.src = src;
        this.dest = dest;
        this.operation = operation;
        this.contents = arguments;
    }

    public Message(Message message) {
        this.src = message.src;
        this.dest = message.dest;
        this.operation = message.operation;
        this.contents = message.contents;
    }
}
