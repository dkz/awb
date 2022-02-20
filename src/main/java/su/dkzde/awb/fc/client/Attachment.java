package su.dkzde.awb.fc.client;

import java.util.Objects;

public final class Attachment {

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
        private Integer sizeBytes;
        private String filename;
        private String extension;
        private boolean deleted = false;
        private boolean spoiler = false;
        private String md5;
        private Builder() {}
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
                    Objects.requireNonNull(sizeBytes),
                    Objects.requireNonNull(filename),
                    Objects.requireNonNull(extension),
                    deleted,
                    spoiler,
                    Objects.requireNonNull(md5));
        }
    }

    private Attachment(
            int sizeBytes,
            String filename,
            String extension,
            boolean deleted,
            boolean spoiler,
            String md5) {

        this.sizeBytes = sizeBytes;
        this.filename = filename;
        this.extension = extension;
        this.deleted = deleted;
        this.spoiler = spoiler;
        this.md5 = md5;
    }

}
