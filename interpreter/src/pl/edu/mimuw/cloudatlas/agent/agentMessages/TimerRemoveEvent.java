package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.agent.MessageContent;

import static pl.edu.mimuw.cloudatlas.agent.MessageContent.Operation.TIMER_REMOVE_EVENT;

public class TimerRemoveEvent extends MessageContent {
    public long id;

    public TimerRemoveEvent(long id) {
        this.id = id;

        operation = TIMER_REMOVE_EVENT;
    }
}
