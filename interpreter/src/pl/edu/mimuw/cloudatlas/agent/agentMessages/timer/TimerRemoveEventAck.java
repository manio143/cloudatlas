package pl.edu.mimuw.cloudatlas.agent.agentMessages.timer;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;

import static pl.edu.mimuw.cloudatlas.agent.utility.Operation.TIMER_REMOVE_EVENT_ACK;

public class TimerRemoveEventAck extends MessageContent {
    public long id;

    public TimerRemoveEventAck (long id) {
        this.id = id;

        operation = TIMER_REMOVE_EVENT_ACK;
    }
}
