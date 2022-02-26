package su.dkzde.awb;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import su.dkzde.awb.fc.client.Board;

import java.util.Map;
import java.util.function.Consumer;

@Component
@DispatcherPriority(100)
public class AdminController implements DispatchController {

    @Autowired
    private Permissions permissions;

    @Override
    public boolean consumeMessageEvent(MessageCreateEvent event) {
        if (shouldConsume(event)) {
            Consumer<MessageCreateEvent> command = commands.get(event.getMessageContent());
            if (command != null) {
                command.accept(event);
            }
            return true;
        } else {
            return false;
        }
    }

    private Map<String, Consumer<MessageCreateEvent>> commands = Map.ofEntries(
            Map.entry("awb vt-embeds enable", event -> {
                TextChannel channel = event.getChannel();
                permissions.setEmbedsPermitted(channel.getIdAsString(), Board.vt, true);
            }),
            Map.entry("awb vt-embeds disable", event -> {
                TextChannel channel = event.getChannel();
                permissions.setEmbedsPermitted(channel.getIdAsString(), Board.vt, false);
            })
    );

    private boolean shouldConsume(MessageCreateEvent event) {
        MessageAuthor author = event.getMessageAuthor();
        return author.isBotOwner()
            && event.getMessageContent().startsWith("awb");
    }
}
