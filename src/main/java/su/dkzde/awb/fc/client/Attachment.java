package su.dkzde.awb.fc.client;

import java.net.URI;
import java.util.Objects;

public final class Attachment {

    private final Board board;

    private final long id;
    private final int sizeBytes;
    private final String filename;
    private final String extension;
    private final boolean deleted;
    private final boolean spoiler;
    private final String md5;

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Board board;
        private Long id;
        private Integer sizeBytes;
        private String filename;
        private String extension;
        private boolean deleted = false;
        private boolean spoiler = false;
        private String md5;
        private Builder() {}
        public Builder setId(long id) {
            this.id = id;
            return this;
        }
        public Builder setBoard(Board board) {
            this.board = board;
            return this;
        }
        public Builder setSizeBytes(int bytes) {
            this.sizeBytes = bytes;
            return this;
        }
        public Builder setFilename(String filename) {
            this.filename = filename;
            return this;
        }
        public Builder setExtension(String extension) {
            this.extension = extension;
            return this;
        }
        public Builder setDeleted(boolean deleted) {
            this.deleted = deleted;
            return this;
        }
        public Builder setSpoiler(boolean spoiler) {
            this.spoiler = spoiler;
            return this;
        }
        public Builder setMd5(String md5) {
            this.md5 = md5;
            return this;
        }
        public Attachment build() {
            return new Attachment(
                    Objects.requireNonNull(board),
                    Objects.requireNonNull(id),
                    Objects.requireNonNull(sizeBytes),
                    Objects.requireNonNull(filename),
                    Objects.requireNonNull(extension),
                    deleted,
                    spoiler,
                    Objects.requireNonNull(md5));
        }
    }

    public String filename() {
        return filename;
    }

    public String extension() {
        return extension;
    }

    public URI thumbnail() {
        return board.thumbnail(id);
    }

    public URI location() {
        return board.attachment(id, extension);
    }

    private Attachment(
            Board board,
            long id,
            int sizeBytes,
            String filename,
            String extension,
            boolean deleted,
            boolean spoiler,
            String md5) {

        this.board = board;
        this.id = id;
        this.sizeBytes = sizeBytes;
        this.filename = filename;
        this.extension = extension;
        this.deleted = deleted;
        this.spoiler = spoiler;
        this.md5 = md5;
    }

}
