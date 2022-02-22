package su.dkzde.awb.fc;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.dkzde.awb.fc.client.Board;
import su.dkzde.awb.fc.client.Page;
import su.dkzde.awb.fc.client.Thread;

public interface ReactiveAccess {
    Flux<Page> loadBoard(Board board);
    Mono<Thread> loadThread(Board board, long op);
}
