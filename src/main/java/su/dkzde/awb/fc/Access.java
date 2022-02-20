package su.dkzde.awb.fc;

import su.dkzde.awb.fc.client.Board;
import su.dkzde.awb.fc.client.Thread;
import su.dkzde.awb.fc.client.Threads;

import java.util.concurrent.CompletableFuture;

public interface Access {
    CompletableFuture<Threads> loadThreads(Board board);
    CompletableFuture<Thread> loadThread(Board board, long op);
}
