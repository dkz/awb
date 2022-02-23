package su.dkzde.awb.fc.client;

import org.springframework.lang.Nullable;

import java.net.URI;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public final class Post {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Board board;
        private Long number;
        private Long thread;
        private Instant posted;
        private @Nullable Comment comment;
        private @Nullable CaptionCode caption;
        private @Nullable Attachment attachment;
        private Builder() {}
        public Builder setThread(long number) {
            this.thread = number;
            return this;
        }
        public Builder setBoard(Board board) {
            this.board = board;
            return this;
        }
        public Builder setNumber(long number) {
            this.number = number;
            return this;
        }
        public Builder setPosted(Instant posted) {
            this.posted = posted;
            return this;
        }
        public Builder setComment(@Nullable Comment comment) {
            this.comment = comment;
            return this;
        }
        public Builder setCaption(@Nullable CaptionCode caption) {
            this.caption = caption;
            return this;
        }
        public Builder setAttachment(@Nullable Attachment attachment) {
            this.attachment = attachment;
            return this;
        }
        public Post build() {
            return new Post(
                    Objects.requireNonNull(board),
                    Objects.requireNonNull(thread),
                    Objects.requireNonNull(number),
                    Objects.requireNonNull(posted),
                    comment,
                    caption,
                    attachment);
        }
    }

    private final Board board;
    private final long thread;
    private final long number;
    private final Instant posted;
    private final @Nullable Comment comment;
    private final @Nullable CaptionCode caption;
    private final @Nullable Attachment attachment;

    public long number() {
        return number;
    }

    public Instant posted() {
        return posted;
    }

    public synchronized Optional<Comment> comment() {
        return Optional.ofNullable(comment);
    }

    public Optional<CaptionCode> caption() {
        return Optional.ofNullable(caption);
    }

    public Optional<Attachment> attachment() {
        return Optional.ofNullable(attachment);
    }

    public URI location() {
        return board.post(thread, number);
    }

    private Post(
            Board board,
            long thread,
            long number,
            Instant posted,
            @Nullable Comment comment,
            @Nullable CaptionCode caption,
            @Nullable Attachment attachment) {

        this.board = board;
        this.thread = thread;
        this.number = number;
        this.posted = posted;
        this.comment = comment;
        this.caption = caption;
        this.attachment = attachment;
    }
}
