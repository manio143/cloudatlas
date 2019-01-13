package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import java.io.Serializable;

public class Message implements Serializable {
    public enum Module {
        TIMER, COMMUNICATION, RMI, TESTER
    }

    public final Module src;
    public final Module dest;

    public final MessageContent content;

    public Message(Module src, Module dest, MessageContent content) {
        this.src = src;
        this.dest = dest;
        this.content = content;
    }
}
