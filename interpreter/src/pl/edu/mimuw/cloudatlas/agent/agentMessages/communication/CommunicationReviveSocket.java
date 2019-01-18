package pl.edu.mimuw.cloudatlas.agent.agentMessages.communication;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;

import static pl.edu.mimuw.cloudatlas.agent.utility.MessageContent.Operation.COMMUNICATION_REVIVE_SOCKET;

public class CommunicationReviveSocket extends MessageContent {
    public boolean listening;

    public CommunicationReviveSocket(boolean listening) {
        this.listening = listening;

        operation = COMMUNICATION_REVIVE_SOCKET;
    }
}
