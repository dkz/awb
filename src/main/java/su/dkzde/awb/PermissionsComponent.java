package su.dkzde.awb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import su.dkzde.awb.fc.client.Board;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PermissionsComponent implements Permissions {

    private static final Logger logger = LoggerFactory.getLogger(Permissions.class);

    @Autowired
    private ObjectMapper mapper;

    private enum PermissionType {
        channel_embeds
    }

    private record Permission(String channel, Board board, PermissionType permission) {}

    private final ConcurrentHashMap<Permission, Boolean> permissions = new ConcurrentHashMap<>();

    @Override
    public boolean embedsPermitted(String channel, Board board) {
        return Boolean.TRUE.equals(
                permissions.get(new Permission(channel, board, PermissionType.channel_embeds)));
    }

    @Async
    @Override
    public void setEmbedsPermitted(String channel, Board board, boolean permitted) {
        permissions.put(new Permission(channel, board, PermissionType.channel_embeds), permitted);
        export();
    }

    private void export() {
        try {
            ObjectWriter writer = mapper.writer();
            writer.writeValue(configuration(), exportJson());
        } catch (IOException exception) {
            logger.error("Unable to export permissions", exception);
        }
    }

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
    }

    private File configuration() throws IOException {
        Path home = Paths.get(System.getProperty("user.home"));
        Path directory = Files.createDirectories(home.resolve(".awb"));
        return directory.resolve("Permissions.json").toFile();
    }

    private Json exportJson() {
        Json json = new Json();
        permissions.forEach((key, enabled) -> {
            json.permissions
                    .computeIfAbsent(key.channel, _k -> new HashMap<>())
                    .computeIfAbsent(key.board, _k -> new HashMap<>())
                    .put(key.permission, enabled);
        });
        return json;
    }

    private void importJson(Json json) {
        json.permissions.forEach((channel, boards) ->
                boards.forEach((board, perms) ->
                        perms.forEach((perm, enabled) ->
                                permissions.put(new Permission(channel, board, perm), enabled))));
    }

    private static class Json {
        public Map<String, Map<Board, Map<PermissionType, Boolean>>> permissions = new HashMap<>();
    }
}
