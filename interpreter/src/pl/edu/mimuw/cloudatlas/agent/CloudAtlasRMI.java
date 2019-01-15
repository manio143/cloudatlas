package pl.edu.mimuw.cloudatlas.agent;

import pl.edu.mimuw.cloudatlas.agent.agentExceptions.IncorrectMessageContent;
import pl.edu.mimuw.cloudatlas.agent.agentExceptions.RMIInterrupted;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.*;
import pl.edu.mimuw.cloudatlas.agent.agentModules.RMI;
import pl.edu.mimuw.cloudatlas.cloudAtlasAPI.CloudAtlasAPI;
import pl.edu.mimuw.cloudatlas.model.Attribute;
import pl.edu.mimuw.cloudatlas.model.AttributesMap;
import pl.edu.mimuw.cloudatlas.model.Value;
import pl.edu.mimuw.cloudatlas.model.ValueSet;
import pl.edu.mimuw.cloudatlas.signer.SignedQueryRequest;

import java.util.List;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;

import static pl.edu.mimuw.cloudatlas.agent.Message.Module.RMI;
import static pl.edu.mimuw.cloudatlas.agent.Message.Module.ZMI_KEEPER;
import static pl.edu.mimuw.cloudatlas.agent.MessageContent.Operation;
import static pl.edu.mimuw.cloudatlas.agent.MessageContent.Operation.*;

public class CloudAtlasRMI implements CloudAtlasAPI {
    private SynchronousQueue<MessageContent> rmi;
    private MessageHandler handler;
    private RMI.RMIController controller;

    public CloudAtlasRMI(MessageHandler handler, SynchronousQueue<MessageContent> rmi, RMI.RMIController controller) {
        this.handler = handler;
        this.rmi = rmi;
        this.controller = controller;
    }

    private MessageContent handleMessages(MessageContent content, Operation expected) {
        try {
            controller.waiting = true;

            Message toKeeper = new Message(RMI, ZMI_KEEPER, content);

            handler.addMessage(toKeeper);

            MessageContent received = rmi.take();

            controller.waiting = false;

            switch(received.operation) {
                case RMI_ERROR:
                    throw ((RMIError)received).error;

                default:
                    if (received.operation != expected) {
                        throw new IncorrectMessageContent(expected, received.operation);
                    }
            }

            return received;

        } catch (InterruptedException e) {
            controller.waiting = false;

            throw new RMIInterrupted();
        }
    }

    public synchronized List<String> getZones() {
        ZMIKeeperZones toKeeper = new ZMIKeeperZones();

        MessageContent content = handleMessages(toKeeper, RMI_ZONES);

        return ((RMIZones)content).zones;
    }

    public synchronized AttributesMap getAttributes(String pathName) {
        ZMIKeeperAttributesMap toKeeper = new ZMIKeeperAttributesMap(pathName);

        MessageContent content = handleMessages(toKeeper, RMI_ATTRIBUTES);

        return ((RMIAttributes) content).attributesMap;
    }

    public synchronized Map<String, List<Attribute>> getQueries() {
        ZMIKeeperQueries toKeeper = new ZMIKeeperQueries();

        MessageContent content = handleMessages(toKeeper, RMI_QUERIES);

        return ((RMIQueries) content).queries;
    }

    public synchronized void installQueries(SignedQueryRequest queries) {
        ZMIKeeperInstallQueries toKeeper = new ZMIKeeperInstallQueries(queries);

        handleMessages(toKeeper, RMI_INSTALL_QUERY);
    }

    public synchronized void uninstallQuery(SignedQueryRequest queryName) {
        ZMIKeeperRemoveQueries toKeeper = new ZMIKeeperRemoveQueries(queryName);

        handleMessages(toKeeper, RMI_REMOVE_QUERY);
    }

    public synchronized void setAttribute(String pathName, String attr, Value val) {
        ZMIKeeperSetAttribute toKeeper = new ZMIKeeperSetAttribute(pathName, attr, val);

        handleMessages(toKeeper, RMI_SET_ATTRIBUTE);
    }

    public synchronized void setFallbackContacts(ValueSet contacts) {
        ZMIKeeperFallbackContacts toKeeper = new ZMIKeeperFallbackContacts(contacts);

        handleMessages(toKeeper, RMI_FALLBACK_CONTACTS);
    }
}
