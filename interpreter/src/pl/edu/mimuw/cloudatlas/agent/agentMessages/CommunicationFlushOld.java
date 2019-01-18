package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.agent.MessageContent;

import static pl.edu.mimuw.cloudatlas.agent.MessageContent.Operation.COMMUNICATION_FLUSH_OLD;

public class CommunicationFlushOld extends MessageContent {
    public CommunicationFlushOld() {

        operation = COMMUNICATION_FLUSH_OLD;
    }

    public CommunicationFlushOld copy() {
        return new CommunicationFlushOld();
    }
}
