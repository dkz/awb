package su.dkzde.awb;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Schedulers;
import su.dkzde.awb.fc.CachedAccess;
import su.dkzde.awb.fc.client.Board;
import su.dkzde.awb.fc.client.Post;
import su.dkzde.awb.fc.client.Thread;

import java.util.Objects;
import java.util.Optional;

@Component
public class EmbedDispatcher implements DispatchController {

    @Autowired
    private DiscordApi api;

    @Autowired
    private CachedAccess fcc;

    @Autowired
    private Permissions permissions;

    @Override
    public boolean consumeMessageEvent(MessageCreateEvent event) {
        TextChannel channel = event.getChannel();
        for (Board board : Board.values()) {
            if (permissions.embedsPermitted(channel.getIdAsString(), board)) {
                Optional<Board.Link> parsed = board.parseLink(event.getMessageContent());
                parsed.ifPresent(link -> {
                    fcc.fetchThread(board, link.thread()).subscribeOn(Schedulers.boundedElastic()).subscribe(thread -> {
                        if (link.post() == null) {
                            channel.sendMessage(embedOriginalPost(thread));
                        } else thread.post(link.post()).ifPresent(post -> {
                            channel.sendMessage(embedReply(thread, post));
                        });
                    });
                });
            }
        }
        return false;
    }

    private EmbedBuilder embedReply(Thread thread, Post post) {
        EmbedBuilder embed = embed(post);
        embed.setTitle(String.format("Reply to #%d", thread.number()));
        embed.setUrl(Objects.toString(post.location()));
        return embed;
    }

    private EmbedBuilder embedOriginalPost(Thread thread) {

        EmbedBuilder embed = embed(thread.op())
                .setFooter(String.format("%d / %d / %d",
                        thread.replies(),
                        thread.images(),
                        thread.ips()));

        embed.setUrl(Objects.toString(thread.location()));
        thread.subject().ifPresentOrElse(subject -> {
            embed.setTitle(String.format("Thread #%d: %s", thread.number(), subject));
        }, () -> {
            embed.setTitle(String.format("Thread #%d", thread.number()));
        });

        return embed;
    }

    private EmbedBuilder embed(Post post) {
        EmbedBuilder embed = new EmbedBuilder().setTimestamp(post.posted());
        post.attachment().ifPresent(attachment -> {
            embed.setThumbnail(Objects.toString(attachment.location()));
        });
        post.comment().ifPresent(comment -> {
            embed.setDescription(comment.text());
        });
        return embed;
    }
}
