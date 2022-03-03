package su.dkzde.awb.fc;

import reactor.core.publisher.Mono;
import su.dkzde.awb.fc.client.Board;
import su.dkzde.awb.fc.client.Thread;

public interface CachedAccess {
    Mono<Thread> fetchThread(Board board, long op);
    void addListener(CacheEventListener listener);
}
