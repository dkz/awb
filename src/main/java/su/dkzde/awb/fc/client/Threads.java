package su.dkzde.awb.fc.client;

import java.time.Instant;
import java.util.*;

public final class Threads {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Builder() {}
        private final ArrayList<Item> threads = new ArrayList<>();
        public Builder add(Item thread) {
            threads.add(thread);
            return this;
        }
        public Threads build() {
            HashMap<Long, Item> map = new HashMap<>();
            for (Item thread : threads) {
                map.put(thread.number, thread);
            }
            return new Threads(Collections.unmodifiableMap(map));
        }
    }

    public Set<Long> ops() {
        return threads.keySet();
    }

    public static final class Item {

        public final long number;

        public final int page;
        public final int replies;
        public final Instant updated;

        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {
            private Long number;
            private Integer page;
            private Integer replies;
            private Instant updated;
            private Builder() {}
            public Builder setNumber(long number) {
                this.number = number;
                return this;
            }
            public Builder setPage(int page) {
                this.page = page;
                return this;
            }
            public Builder setReplies(int replies) {
                this.replies = replies;
                return this;
            }
            public Builder setUpdated(Instant updated) {
                this.updated = updated;
                return this;
            }
            public Item build() {
                return new Item(
                        Objects.requireNonNull(number),
                        Objects.requireNonNull(page),
                        Objects.requireNonNull(replies),
                        Objects.requireNonNull(updated));
            }
        }

        private Item(
                long number,
                int page,
                int replies,
                Instant updated) {
            this.number = number;
            this.page = page;
            this.replies = replies;
            this.updated = updated;
        }
    }

    private final Map<Long, Item> threads;

    private Threads(Map<Long, Item> threads) {
        this.threads = threads;
    }
}
