package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import java.io.Serializable;

public abstract class MessageContent implements Serializable {
    public enum Operation {
        TIMER_ADD_EVENT, TIMER_REMOVE_EVENT, TIMER_ADD_EVENT_ACK, TIMER_REMOVE_EVENT_ACK, COMMUNICATION_SEND
    }

    public Operation operation;
}
