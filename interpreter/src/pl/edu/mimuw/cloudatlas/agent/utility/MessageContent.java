package pl.edu.mimuw.cloudatlas.agent.utility;

import pl.edu.mimuw.cloudatlas.agent.agentMessages.CopyNotImplementedException;

import java.io.Serializable;

public abstract class MessageContent implements Serializable {
    public enum Operation {
        CONTENT_PLACEHOLDER,
        TIMER_ADD_EVENT,
        TIMER_REMOVE_EVENT,
        TIMER_SCHEDULE_NOTIFICATION,
        TIMER_ADD_EVENT_ACK,
        TIMER_REMOVE_EVENT_ACK,
        COMMUNICATION_SEND,
        COMMUNICATION_REVIVE_SOCKET,
        COMMUNICATION_FLUSH_OLD,
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
        ZMI_KEEPER_FRESHNESS_FOR_CONTACT,
        ZMI_KEEPER_SIBLINGS_FOR_GOSSIP,
        ZMI_KEEPER_PROVIDE_DETAILS,
        ZMI_KEEPER_UPDATE_ZMI,
        ZMI_KEEPER_RECOMPUTE_QUERIES,
        ZMI_KEEPER_CLEANUP,
        GOSSIP_NEXT,
        GOSSIP_SIBLINGS,
        GOSSIP_CONTACTS,
        GOSSIP_FRESHNESS_TO_SEND,
        GOSSIP_INTER_FRESHNESS_START,
        GOSSIP_SIBLINGS_FRESHNESS,
        GOSSIP_INTER_FRESHNESS_RESPONSE,
        GOSSIP_REQUEST_DETAILS,
        GOSSIP_PROVIDE_DETAILS,
        GOSSIP_UPDATE,
    }

    public Operation operation;

    public MessageContent() {

    }

    public MessageContent copy() {
        throw new CopyNotImplementedException(operation);
    }

    public boolean isTimed() { return false; }
}
