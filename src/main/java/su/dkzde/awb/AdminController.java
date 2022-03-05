package su.dkzde.awb;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import su.dkzde.awb.fc.client.Board;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
@DispatcherPriority(100)
public class AdminController implements DispatchController {

    @Autowired
    private Permissions permissions;

    @Autowired
    private Subscriptions subscriptions;

    @Override
    public boolean consumeMessageEvent(MessageCreateEvent event) {
        if (shouldConsume(event)) {
            String message = event.getMessageContent();
            String[] command = message.split("\\s+");
            executeCommand(
                    event,
                    new LinkedList<>(Arrays.asList(command)),
                    new LinkedList<>());

            return true;
        } else {
            return false;
        }
    }

    private interface Command {
        void execute(MessageCreateEvent event, LinkedList<String> args);
    }

    private Map<List<String>, Command> commands = Map.ofEntries(
            Map.entry(List.of("awb", "vt-embeds"), this::vtEmbedsCommand),
            Map.entry(List.of("awb", "vt-popular-posts"), this::vtPopularPosts));

    private void executeCommand(MessageCreateEvent event, LinkedList<String> command, LinkedList<String> arguments) {
        while (!command.isEmpty()) {
            Command cmd = commands.get(command);
            if (cmd != null) {
                cmd.execute(event, arguments);
                return;
            } else {
                arguments.addFirst(command.pollLast());
            }
        }
    }

    private void vtEmbedsCommand(MessageCreateEvent event, LinkedList<String> args) {
        String command = args.pollFirst();
        TextChannel channel = event.getChannel();
        if (command != null) switch (command) {
            case "enable" -> {
                permissions.setEmbedsPermitted(channel.getIdAsString(), Board.vt, true);
            }
            case "disable" -> {
                permissions.setEmbedsPermitted(channel.getIdAsString(), Board.vt, false);
            }
            default -> {}
        }
    }

    private void vtPopularPosts(MessageCreateEvent event, LinkedList<String> args) {
        String command = args.pollFirst();
        TextChannel channel = event.getChannel();
        if (command != null) switch (command) {
            case "enable-image-embeds" -> {
                permissions.setPopularPostsImageEmbeds(channel.getIdAsString(), Board.vt, true);
            }
            case "disable-image-embeds" -> {
                permissions.setPopularPostsImageEmbeds(channel.getIdAsString(), Board.vt, false);
            }
            case "enable" -> {
                String threshold = args.pollFirst();
                if (threshold != null) try {
                    subscriptions.subscribePopularPosts(channel.getIdAsString(), Board.vt, Integer.parseInt(threshold));
                } catch (NumberFormatException ignore) {}
            }
            case "disable" -> {
                subscriptions.unsubscribePopularPosts(channel.getIdAsString(), Board.vt);
            }
            default -> {}
        }
    }

    private boolean shouldConsume(MessageCreateEvent event) {
        MessageAuthor author = event.getMessageAuthor();
        return author.isBotOwner()
            && event.getMessageContent().startsWith("awb");
    }
}
