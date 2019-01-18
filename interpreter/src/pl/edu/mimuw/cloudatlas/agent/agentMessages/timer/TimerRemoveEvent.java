package pl.edu.mimuw.cloudatlas.agent.agentMessages.timer;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;

import static pl.edu.mimuw.cloudatlas.agent.utility.Operation.TIMER_REMOVE_EVENT;

public class TimerRemoveEvent extends MessageContent {
    public long id;

    public TimerRemoveEvent(long id) {
        this.id = id;

        operation = TIMER_REMOVE_EVENT;
    }
}
