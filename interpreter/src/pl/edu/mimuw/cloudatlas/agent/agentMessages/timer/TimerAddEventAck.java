package pl.edu.mimuw.cloudatlas.agent.agentMessages.timer;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;

import static pl.edu.mimuw.cloudatlas.agent.utility.MessageContent.Operation.TIMER_ADD_EVENT_ACK;

public class TimerAddEventAck extends MessageContent {
    public long id;

    public TimerAddEventAck (long id) {
        this.id = id;

        operation = TIMER_ADD_EVENT_ACK;
    }
}
