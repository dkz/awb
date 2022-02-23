package su.dkzde.awb.fc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.dkzde.awb.fc.client.Thread;
import su.dkzde.awb.fc.client.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Objects;

@Component
public class ReactiveAccessComponent implements ReactiveAccess {

    @Autowired
    private WebClient client;

    @Override
    public Flux<Page> loadBoard(Board board) {
        RequestHeadersUriSpec<?> request = client.get();
        ResponseSpec response = request.uri(board.boardAPI()).retrieve();
        return response.bodyToFlux(Json.Page.class).map(json -> {
            ArrayList<Page.Thread> threads = new ArrayList<>(json.threads.size());
            for (Json.Thread thread : json.threads) {
                threads.add(new Page.Thread(
                        thread.number,
                        thread.replies,
                        Instant.ofEpochSecond(thread.modifiedTs)));
            }
            return new Page(json.number, threads);
        });
    }

    @Override
    public Mono<Thread> loadThread(Board board, long op) {
        RequestHeadersUriSpec<?> request = client.get();
        ResponseSpec response = request.uri(board.threadAPI(op)).retrieve();
        return response.bodyToMono(Json.ThreadPosts.class).map(json -> {
            Thread.Builder builder = Thread.builder().setBoard(board);
            for (Json.Post source : json.posts) {
                Post.Builder post = Post.builder()
                        .setBoard(board)
                        .setThread(op)
                        .setNumber(source.number)
                        .setCaption(source.caption)
                        .setPosted(Instant.ofEpochSecond(source.time));
                if (source.comment != null) {
                    post.setComment(Comment.create(source.comment));
                }
                if (source.attachmentSize != null) {
                    post.setAttachment(Attachment.builder()
                            .setBoard(board)
                            .setId(source.attachmentId)
                            .setSizeBytes(source.attachmentSize)
                            .setFilename(source.attachmentFilename)
                            .setExtension(source.attachmentExtension)
                            .setDeleted(Objects.equals(1, source.isAttachmentDeleted))
                            .setSpoiler(Objects.equals(1, source.isSpoiler))
                            .setMd5(source.md5)
                            .build());
                }
                if (Objects.equals(0, source.reply)) {
                    builder.setOp(post.build())
                            .setNumber(source.number)
                            .setReplyCount(Objects.requireNonNullElse(source.threadReplyCount, 0))
                            .setImageCount(Objects.requireNonNullElse(source.threadImageCount, 0))
                            .setUniqueIds(Objects.requireNonNullElse(source.uniqueIps, 0))
                            .setSticky(Objects.equals(1, source.isThreadSticky))
                            .setClosed(Objects.equals(1, source.isThreadClosed))
                            .setBumpLimitReached(Objects.equals(1, source.isBumpLimitReached))
                            .setImageLimitReached(Objects.equals(1, source.isImageLimitReached))
                            .setSubject(source.subject);
                    if (Objects.equals(1, source.isThreadArchived)) {
                        builder.setArchived(Instant.ofEpochSecond(source.threadArchivedTs));
                    }
                } else {
                    builder.addReply(post.build());
                }
            }
            return builder.build();
        });
    }
}
