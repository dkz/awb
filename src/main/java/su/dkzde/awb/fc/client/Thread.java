package su.dkzde.awb.fc.client;

import org.springframework.lang.Nullable;

import java.net.URI;
import java.time.Instant;
import java.util.*;

public final class Thread {

    private final Board board;

    private final long number;
    private final @Nullable String subject;
    private final Post op;

    private final boolean sticky;
    private final boolean closed;

    private final int imageCount;
    private final int replyCount;
    private final int uniqueIps;

    private final boolean bumpLimitReached;
    private final boolean imageLimitReached;
    private final @Nullable Instant archived;

    private final List<Post> replies;
    private final Map<Long, Post> index;

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Board board;
        private URI location;
        private Long number;
        private @Nullable String subject;
        private Post op;
        private Boolean sticky;
        private Boolean closed;
        private Integer uniqueIps;
        private Integer imageCount;
        private Integer replyCount;
        private Boolean bumpLimitReached;
        private Boolean imageLimitReached;
        private @Nullable Instant archived;
        private final ArrayList<Post> replies = new ArrayList<>();
        private final HashMap<Long, Post> index = new HashMap<>();
        private Builder() {}
        public Builder setBoard(Board board) {
            this.board = board;
            return this;
        }
        public Builder setNumber(long number) {
            this.number = number;
            return this;
        }
        public Builder setSubject(String subject) {
            this.subject = subject;
            return this;
        }
        public Builder setOp(Post op) {
            this.op = op;
            return this;
        }
        public Builder setSticky(boolean sticky) {
            this.sticky = sticky;
            return this;
        }
        public Builder setClosed(boolean closed) {
            this.closed = closed;
            return this;
        }
        public Builder setImageCount(int images) {
            this.imageCount = images;
            return this;
        }
        public Builder setReplyCount(int replies) {
            this.replyCount = replies;
            return this;
        }
        public Builder setUniqueIds(int uniqueIps) {
            this.uniqueIps = uniqueIps;
            return this;
        }
        public Builder setBumpLimitReached(boolean bumpLimitReached) {
            this.bumpLimitReached = bumpLimitReached;
            return this;
        }
        public Builder setImageLimitReached(boolean imageLimitReached) {
            this.imageLimitReached = imageLimitReached;
            return this;
        }
        public Builder setArchived(Instant archived) {
            this.archived = archived;
            return this;
        }
        public Builder addReply(Post post) {
            replies.add(post);
            index.put(post.number(), post);
            return this;
        }
        public Thread build() {
            return new Thread(
                    Objects.requireNonNull(board),
                    Objects.requireNonNull(number),
                    subject,
                    Objects.requireNonNull(op),
                    Objects.requireNonNull(sticky),
                    Objects.requireNonNull(closed),
                    Objects.requireNonNull(imageCount),
                    Objects.requireNonNull(replyCount),
                    Objects.requireNonNull(uniqueIps),
                    Objects.requireNonNull(bumpLimitReached),
                    Objects.requireNonNull(imageLimitReached),
                    archived,
                    List.copyOf(replies),
                    Map.copyOf(index));
        }
    }

    public Board board() {
        return board;
    }

    public long number() {
        return number;
    }

    public Post op() {
        return op;
    }

    public Optional<String> subject() {
        return Optional.ofNullable(subject);
    }

    public int images() {
        return imageCount;
    }

    public int replies() {
        return replyCount;
    }

    public int ips() {
        return uniqueIps;
    }

    public URI location() {
        return board.thread(number);
    }

    public Optional<Post> post(long number) {
        if (number == this.number) {
            return Optional.of(op);
        } else {
            return Optional.ofNullable(index.get(number));
        }
    }

    public LinkedList<Post> listPosts() {
        LinkedList<Post> posts = new LinkedList<>(replies);
        posts.addFirst(op);
        return posts;
    }

    private Thread(
            Board board,
            long number,
            @Nullable String subject,
            Post op,
            boolean sticky,
            boolean closed,
            int imageCount,
            int replyCount,
            int uniqueIps,
            boolean bumpLimitReached,
            boolean imageLimitReached,
            @Nullable Instant archived,
            List<Post> replies,
            Map<Long, Post> index) {

        this.board = board;
        this.number = number;
        this.subject = subject;
        this.op = op;
        this.sticky = sticky;
        this.closed = closed;
        this.replyCount = replyCount;
        this.imageCount = imageCount;
        this.uniqueIps = uniqueIps;
        this.bumpLimitReached = bumpLimitReached;
        this.imageLimitReached = imageLimitReached;
        this.archived = archived;
        this.replies = replies;
        this.index = index;
    }
}
