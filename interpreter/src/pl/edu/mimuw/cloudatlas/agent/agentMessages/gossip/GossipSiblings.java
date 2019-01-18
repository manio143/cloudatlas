package pl.edu.mimuw.cloudatlas.agent.agentMessages.gossip;

import java.io.Serializable;
import java.util.List;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;
import pl.edu.mimuw.cloudatlas.agent.utility.Operation;
import pl.edu.mimuw.cloudatlas.model.PathName;
import pl.edu.mimuw.cloudatlas.model.ValueContact;
import pl.edu.mimuw.cloudatlas.model.ValueTime;

public class GossipSiblings extends MessageContent {
    public final List<Sibling> siblings;

    public static class Sibling implements Serializable {
        public final PathName pathName;
        public final List<ValueContact> contacts;
        public final ValueTime timestamp;
        public Sibling(PathName pathName, List<ValueContact> contacts, ValueTime timestamp) {
            this.pathName = pathName;
            this.contacts = contacts;
            this.timestamp = timestamp;
        }
    }

    public GossipSiblings(List<Sibling> siblings) {
        this.siblings = siblings;

        operation = Operation.GOSSIP_SIBLINGS;
    }
}
