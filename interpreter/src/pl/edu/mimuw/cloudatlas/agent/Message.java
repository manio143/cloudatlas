package pl.edu.mimuw.cloudatlas.agent;

import java.io.Serializable;

public class Message implements Serializable {
    public enum Module {
        TIMER, COMMUNICATION, RMI, ZMI_KEEPER, TESTER, GOSSIP, GOSSIP_STRATEGY
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
