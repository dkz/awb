package su.dkzde.awb;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import su.dkzde.awb.fc.CacheEventListener;
import su.dkzde.awb.fc.CachedAccess;
import su.dkzde.awb.fc.client.Board;
import su.dkzde.awb.fc.client.Post;
import su.dkzde.awb.fc.client.Thread;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SubscriptionsComponent implements Subscriptions {

    private static final Logger logger = LoggerFactory.getLogger(Subscriptions.class);

    @Autowired
    private DiscordApi api;

    @Autowired
    private CachedAccess fcc;

    @Autowired
    private Permissions permissions;

    @Autowired
    private ObjectMapper mapper;

    @PostConstruct
    private void postConstruct() {
        try {
            File configuration = configuration();
            if (configuration.exists()) {
                ObjectReader reader = mapper.reader();
                importJson(reader.readValue(configuration, Json.class));
            }
        } catch (IOException exception) {
            logger.error("Unable to import serialized permissions", exception);
        }
        fcc.addListener(listener);
    }

    private record SubscriptionSettings(boolean popularPosts, int popularPostThreshold) {}

    private final ConcurrentHashMap<Board,
            ConcurrentHashMap<String, SubscriptionSettings>> subscriptions = new ConcurrentHashMap<>();

    private final CacheEventListener listener = new CacheEventListener() {
        @Override public void emitPostQuotesEvent(Thread thread, Post post, int quotesTotal, int quotesNew) {
            ConcurrentHashMap<String, SubscriptionSettings> settings = subscriptions.get(thread.board());
            if (settings != null) settings.forEach((channel, config) -> {
                if (config.popularPosts) {
                    if (config.popularPostThreshold <= quotesTotal && config.popularPostThreshold > quotesTotal - quotesNew) {
                        api.getTextChannelById(channel).ifPresent(ch -> {
                            ch.sendMessage(popularPost(channel, thread, post, quotesTotal));
                        });
                    }
                }
            });
        }
    };

    private EmbedBuilder popularPost(String channel, Thread thread, Post post, int quotes) {
        EmbedBuilder embed = new EmbedBuilder().setTimestamp(post.posted());
        post.attachment().ifPresent(attachment -> {
            if (permissions.popularPostsImageEmbeds(channel, thread.board())) {
                embed.setImage(Objects.toString(attachment.location()));
            } else {
                embed.setThumbnail(Objects.toString(attachment.location()));
            }
        });
        post.comment().ifPresent(comment -> {
            embed.setDescription(comment.text());
        });
        embed.setTitle(String.format("Popular post on %s #%d", thread.board(), post.number()));
        embed.setUrl(Objects.toString(post.location()));
        embed.setFooter(String.format("%d (You)s", quotes));
        return embed;
    }

    @Override
    public void subscribePopularPosts(String channel, Board board, int threshold) {
        ConcurrentHashMap<String, SubscriptionSettings> settings = subscriptions.computeIfAbsent(board, _k -> new ConcurrentHashMap<>());
        while (true) {
            SubscriptionSettings config = settings.get(channel);
            if (config == null) {
                if (settings.putIfAbsent(channel, new SubscriptionSettings(true, threshold)) == null) {
                    export();
                    return;
                }
            } else if (settings.replace(channel, config, new SubscriptionSettings(true, threshold))) {
                export();
                return;
            }
        }
    }

    @Override
    public void unsubscribePopularPosts(String channel, Board board) {
        ConcurrentHashMap<String, SubscriptionSettings> settings = subscriptions.computeIfAbsent(board, _k -> new ConcurrentHashMap<>());
        while (true) {
            SubscriptionSettings config = settings.get(channel);
            if (config == null) {
                if (settings.putIfAbsent(channel, new SubscriptionSettings(false, 0)) == null) {
                    export();
                    return;
                }
            } else if (settings.replace(channel, config, new SubscriptionSettings(false, 0))) {
                export();
                return;
            }
        }
    }

    private void export() {
        try {
            ObjectWriter writer = mapper.writer();
            writer.writeValue(configuration(), exportJson());
        } catch (IOException exception) {
            logger.error("Unable to export permissions", exception);
        }
    }

    private File configuration() throws IOException {
        Path home = Paths.get(System.getProperty("user.home"));
        Path directory = Files.createDirectories(home.resolve(".awb"));
        return directory.resolve("Subscriptions.json").toFile();
    }

    private Json exportJson() {
        Json json = new Json();
        subscriptions.forEach((board, settings) -> {
            HashMap<String, SubscriptionSettings> map = new HashMap<>();
            json.subscriptions.put(board, map);
            map.putAll(settings);
        });
        return json;
    }

    private void importJson(Json json) {
        json.subscriptions.forEach((board, settings) -> {
            ConcurrentHashMap<String, SubscriptionSettings> imported = new ConcurrentHashMap<>(settings);
            subscriptions.put(board, imported);
        });
    }

    private static final class Json {
        @JsonProperty
        Map<Board, Map<String, SubscriptionSettings>> subscriptions = new HashMap<>();
    }
}
