package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import java.io.Serializable;

public abstract class MessageContent implements Serializable {
    public enum Operation {
        TIMER_ADD_EVENT,
        TIMER_REMOVE_EVENT,
        TIMER_ADD_EVENT_ACK,
        TIMER_REMOVE_EVENT_ACK,
        COMMUNICATION_SEND,
        RMI_ZONES,
        RMI_ATTRIBUTES,
        RMI_QUERIES,
        RMI_ERROR,
        ZMI_KEEPER_ZONES,
        ZMI_KEEPER_QUERIES,
        ZMI_KEEPER_ATTRIBUTES,
        ZMI_KEEPER_INSTALL_QUERY,
        ZMI_KEEPER_REMOVE_QUERY,
        ZMI_KEEPER_FALLBACK_CONTACT,
        ZMI_KEEPER_SET_ATTRIBUTE
    }

    public Operation operation;
}
