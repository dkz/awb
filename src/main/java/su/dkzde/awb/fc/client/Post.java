package su.dkzde.awb.fc.client;

import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public final class Post {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long number;
        private Instant posted;
        private String comment;
        private @Nullable CaptionCode caption;
        private @Nullable Attachment attachment;
        private Builder() {}
        public Builder setNumber(long number) {
            this.number = number;
            return this;
        }
        public Builder setPosted(Instant posted) {
            this.posted = posted;
            return this;
        }
        public Builder setComment(String comment) {
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
                    Objects.requireNonNull(number),
                    Objects.requireNonNull(posted),
                    Objects.requireNonNull(comment),
                    caption,
                    attachment);
        }
    }

    private final long number;
    private final Instant posted;
    private final String comment;
    private final @Nullable CaptionCode caption;
    private final @Nullable Attachment attachment;

    public long number() {
        return number;
    }

    public Instant posted() {
        return posted;
    }

    public String comment() {
        return comment;
    }

    public Optional<CaptionCode> caption() {
        return Optional.ofNullable(caption);
    }

    public Optional<Attachment> attachment() {
        return Optional.ofNullable(attachment);
    }

    private Post(
            long number,
            Instant posted,
            String comment,
            @Nullable CaptionCode caption,
            @Nullable Attachment attachment) {

        this.number = number;
        this.posted = posted;
        this.comment = comment;
        this.caption = caption;
        this.attachment = attachment;
    }
}
