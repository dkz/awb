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

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class EmbedDispatcher implements DispatchController {

    @Autowired
    private DiscordApi api;

    @Autowired
    private CachedAccess fcc;

    @Override
    public boolean consumeMessageEvent(MessageCreateEvent event) {
        TextChannel channel = event.getChannel();
        for (Board board : Board.values()) {
            Pattern pattern = board.pattern();
            Matcher matcher = pattern.matcher(event.getMessageContent());
            if (matcher.find()) {
                String tid = matcher.group("thread");
                String pid = matcher.group("post");
                fcc.fetchThread(board, Long.parseLong(tid)).subscribeOn(Schedulers.boundedElastic()).subscribe(thread -> {
                    if (pid == null) {
                        Post post = thread.op();
                        EmbedBuilder embed = embed(post)
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
                        channel.sendMessage(embed);
                    } else thread.post(Long.parseLong(pid)).ifPresent(post -> {
                        EmbedBuilder embed = embed(post);
                        embed.setTitle(String.format("Reply to #%d", thread.number()));
                        embed.setUrl(Objects.toString(post.location()));
                        channel.sendMessage(embed);
                    });
                });
                return false;
            }
        }
        return false;
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
