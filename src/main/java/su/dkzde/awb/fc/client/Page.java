package su.dkzde.awb.fc.client;

import java.time.Instant;
import java.util.List;

public record Page(int number, List<Thread> threads) {
    public record Thread(long number, int replies, Instant modified) {}
}