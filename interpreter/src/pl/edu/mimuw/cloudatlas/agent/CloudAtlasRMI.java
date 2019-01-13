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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;

import static pl.edu.mimuw.cloudatlas.agent.agentMessages.Message.Module.RMI;
import static pl.edu.mimuw.cloudatlas.agent.agentMessages.Message.Module.ZMI_KEEPER;
import static pl.edu.mimuw.cloudatlas.agent.agentMessages.MessageContent.Operation.RMI_ZONES;

public class CloudAtlasRMI implements CloudAtlasAPI {
    private SynchronousQueue<MessageContent> rmi;
    private MessageHandler handler;
    private RMI.RMIController controller;

    public CloudAtlasRMI(MessageHandler handler, SynchronousQueue<MessageContent> rmi, RMI.RMIController controller) {
        this.handler = handler;
        this.rmi = rmi;
        this.controller = controller;
    }

    public synchronized List<String> getZones() {
//        List<String> zones = new LinkedList<>();
//
//        zones.add("incorrect");
//
//        return zones;

        try {
            controller.waiting = true;

            ZMIKeeperZones queryContent = new ZMIKeeperZones();

            Message toKeeper = new Message(RMI, ZMI_KEEPER, queryContent);

            handler.addMessage(toKeeper);

            MessageContent content = rmi.take();

            controller.waiting = false;

            switch(content.operation) {
                case RMI_ZONES:
                    return ((RMIZones)content).zones;

                case RMI_ERROR:
                    throw ((RMIError)content).error;

                default:
                    throw new IncorrectMessageContent(RMI_ZONES, content.operation);
            }

        } catch (InterruptedException e) {
            controller.waiting = false;

            throw new RMIInterrupted();
        }
    }

    public synchronized AttributesMap getAttributes(String pathName) {
        AttributesMap map = new AttributesMap();
        return map;
    }

    public synchronized Map<String, List<Attribute>> getQueries() {
        Map<String, List<Attribute>> map = new HashMap<>();
        return map;
    }

    public synchronized void installQueries(SignedQueryRequest queries) {

    }

    public synchronized void uninstallQuery(SignedQueryRequest queryName) {

    }

    public synchronized void setAttribute(String pathName, String attr, Value val) {

    }

    public synchronized void setFallbackContacts(ValueSet contacts) {

    }
}
