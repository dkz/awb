package su.dkzde.awb;

import org.javacord.api.event.message.MessageCreateEvent;

public interface DispatchController {
    boolean consumeMessageEvent(MessageCreateEvent event);
}
