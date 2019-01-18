package pl.edu.mimuw.cloudatlas.agent.agentMessages.communication;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;

import static pl.edu.mimuw.cloudatlas.agent.utility.MessageContent.Operation.COMMUNICATION_FLUSH_OLD;

public class CommunicationFlushOld extends MessageContent {
    public CommunicationFlushOld() {

        operation = COMMUNICATION_FLUSH_OLD;
    }

    public CommunicationFlushOld copy() {
        return new CommunicationFlushOld();
    }
}
