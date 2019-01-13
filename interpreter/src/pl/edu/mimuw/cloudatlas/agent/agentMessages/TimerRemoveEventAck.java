package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import static pl.edu.mimuw.cloudatlas.agent.agentMessages.MessageContent.Operation.TIMER_REMOVE_EVENT_ACK;

public class TimerRemoveEventAck extends MessageContent {
    public long id;

    public TimerRemoveEventAck (long id) {
        this.id = id;

        operation = TIMER_REMOVE_EVENT_ACK;
    }
}