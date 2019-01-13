package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import static pl.edu.mimuw.cloudatlas.agent.agentMessages.MessageContent.Operation.TIMER_ADD_EVENT_ACK;

public class TimerAddEventAck extends MessageContent {
    public long id;

    public TimerAddEventAck (long id) {
        this.id = id;

        operation = TIMER_ADD_EVENT_ACK;
    }
}
