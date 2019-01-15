package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.agent.MessageContent;

public class GossipStrategyNext extends MessageContent {
    public GossipStrategyNext() {
        operation = Operation.GOSSIP_STRATEGY_NEXT;
    }
}
