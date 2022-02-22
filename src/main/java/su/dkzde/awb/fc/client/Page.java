package su.dkzde.awb.fc.client;

import java.time.Instant;
import java.util.List;

public final class Page {

    public record Thread(long number, int replies, Instant modified) {}

    public static Page create(int number, List<Thread> threads) {
        return new Page(number, List.copyOf(threads));
    }

    private int number;
    private final List<Thread> threads;

    private Page(int number, List<Thread> threads) {
        this.number = number;
        this.threads = threads;
    }
}
