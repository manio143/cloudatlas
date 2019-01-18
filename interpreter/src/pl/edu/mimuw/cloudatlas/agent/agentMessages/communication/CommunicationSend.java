package pl.edu.mimuw.cloudatlas.agent.agentMessages.communication;

import pl.edu.mimuw.cloudatlas.agent.utility.Message;
import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;

import java.net.InetAddress;

import static pl.edu.mimuw.cloudatlas.agent.utility.Operation.COMMUNICATION_SEND;

public class CommunicationSend extends MessageContent {
    public InetAddress ip;
    public Message message;

    public CommunicationSend(InetAddress ip, Message message) {
        this.ip = ip;
        this.message = message;

        operation = COMMUNICATION_SEND;
    }
}
