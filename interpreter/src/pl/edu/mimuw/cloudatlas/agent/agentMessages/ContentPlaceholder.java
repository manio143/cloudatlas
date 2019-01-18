package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;

import static pl.edu.mimuw.cloudatlas.agent.utility.MessageContent.Operation.CONTENT_PLACEHOLDER;

public class ContentPlaceholder extends MessageContent {
    public ContentPlaceholder() {
        operation = CONTENT_PLACEHOLDER;
    }
}
