package pl.edu.mimuw.cloudatlas.agent.utility;

import java.io.Serializable;

public class Message implements Serializable {

    public final ModuleName src;
    public final ModuleName dest;

    public final MessageContent content;

    public Message(ModuleName src, ModuleName dest, MessageContent content) {
        this.src = src;
        this.dest = dest;
        this.content = content;
    }
}
