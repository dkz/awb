package su.dkzde.awb.fc.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public final class Client {

    public static Client create(HttpClient client) {
        return new Client(Objects.requireNonNull(client));
    }

    public CompletableFuture<Threads> loadThreads(Board board) {
        HttpRequest request = HttpRequest.newBuilder(board.threadsAPI()).GET().build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream()).thenApply(response -> {
            try (InputStream stream = response.body()) {
                Threads.Builder builder = Threads.builder();
                JsonSupport json = support.get();
                List<ThreadsPageJson> pages = json.threads.readValue(stream);
                for (ThreadsPageJson page : pages) {
                    for (ThreadsItemJson thread : page.threads) {
                        builder.add(Threads.Item.builder()
                                .setNumber(thread.number)
                                .setPage(page.number)
                                .setReplies(thread.replies)
                                .setUpdated(Instant.ofEpochSecond(thread.modifiedTs))
                                .build());
                    }
                }
                return builder.build();
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    private static final class ThreadsPageJson {
        @JsonProperty("page")
        public int number;
        public List<ThreadsItemJson> threads;
    }

    private static final class ThreadsItemJson {
        @JsonProperty("no")
        public long number;
        @JsonProperty("last_modified")
        public long modifiedTs;
        public int replies;
    }

    public CompletableFuture<Thread> loadThread(Board board, long op) {
        HttpRequest request = HttpRequest.newBuilder(board.threadAPI(op)).GET().build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream()).thenApply(response -> {
            try (InputStream stream = response.body()) {
                Thread.Builder builder = Thread.builder().setBoard(board);
                JsonSupport json = support.get();
                ThreadJson thread = json.mapper.readValue(stream, ThreadJson.class);
                for (PostJson source : thread.posts) {
                    Post.Builder post = Post.builder()
                            .setNumber(source.number)
                            .setComment(source.comment)
                            .setCaption(source.caption)
                            .setPosted(Instant.ofEpochSecond(source.time));
                    if (source.attachmentSize != null) {
                        post.setAttachment(Attachment.builder()
                                .setSizeBytes(source.attachmentSize)
                                .setFilename(source.attachmentFilename)
                                .setExtension(source.attachmentExtension)
                                .setDeleted(Objects.equals(1, source.attachmentDeleted))
                                .setSpoiler(Objects.equals(1, source.spoiler))
                                .setMd5(source.md5)
                                .build());
                    }
                    if (Objects.equals(0, source.reply)) {
                        builder.setOp(post.build())
                                .setNumber(source.number)
                                .setUniqueIds(source.uniqueIps)
                                .setSticky(Objects.equals(1, source.opSticky))
                                .setClosed(Objects.equals(1, source.opClosed))
                                .setBumpLimitReached(Objects.equals(1, source.bumpLimitReached))
                                .setImageLimitReached(Objects.equals(1, source.imageLimitReached))
                                .setSubject(source.subject);
                        if (Objects.equals(1, source.threadArchived)) {
                            builder.setArchived(Instant.ofEpochSecond(source.threadArchived));
                        }
                    } else {
                        builder.addReply(post.build());
                    }
                }
                return builder.build();
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    private static final class ThreadJson {
        public List<PostJson> posts;
    }

    private static final class PostJson {

        @JsonProperty("no")
        public long number;

        public long time;
        public String name;
        @JsonProperty("capcode")
        public CaptionCode caption;
        @JsonProperty("com")
        public String comment;
        @JsonProperty("resto")
        public Integer reply;

        // Properties that are applied only to OP
        @JsonProperty("sub")
        public String subject;
        @JsonProperty("sticky")
        public Integer opSticky;
        @JsonProperty("closed")
        public Integer opClosed;
        @JsonProperty("replies")
        public Integer opReplyCount;
        @JsonProperty("images")
        public Integer opImageCount;
        @JsonProperty("bumplimit")
        public Integer bumpLimitReached;
        @JsonProperty("imagelimit")
        public Integer imageLimitReached;
        @JsonProperty("unique_ips")
        public Integer uniqueIps;
        @JsonProperty("archived")
        public Integer threadArchived;
        @JsonProperty("archived_on")
        public Long threadArchivedTs;

        // Attachment file properties
        @JsonProperty("filename")
        public String attachmentFilename;
        @JsonProperty("ext")
        public String attachmentExtension;
        @JsonProperty("fsize")
        public Integer attachmentSize;
        @JsonProperty("filedeleted")
        public Integer attachmentDeleted;
        public Integer spoiler;
        public String md5;
    }

    private static final class JsonSupport {
        private final ObjectMapper mapper;
        private final ObjectReader threads;
        private JsonSupport() {
            mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
            threads = mapper.readerForListOf(ThreadsPageJson.class);
        }
    }

    private static final ThreadLocal<JsonSupport> support = ThreadLocal.withInitial(JsonSupport::new);
    private final HttpClient client;

    private Client(HttpClient client) {
        this.client = client;
    }
}
