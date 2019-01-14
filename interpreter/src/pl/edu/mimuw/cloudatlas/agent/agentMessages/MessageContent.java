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
        RMI_INSTALL_QUERY,
        RMI_REMOVE_QUERY,
        RMI_FALLBACK_CONTACTS,
        RMI_SET_ATTRIBUTE,
        ZMI_KEEPER_ZONES,
        ZMI_KEEPER_QUERIES,
        ZMI_KEEPER_ATTRIBUTES,
        ZMI_KEEPER_SIBLINGS,
        ZMI_KEEPER_INSTALL_QUERY,
        ZMI_KEEPER_REMOVE_QUERY,
        ZMI_KEEPER_FALLBACK_CONTACTS,
        ZMI_KEEPER_FALLBACK_CONTACTS_GOSSIP,
        ZMI_KEEPER_SET_ATTRIBUTE,
        GOSSIP_NEXT,
        GOSSIP_SIBLINGS,
        GOSSIP_CONTACTS,
        GOSSIP_STRATEGY_NEXT
    }

    public Operation operation;
}
