package su.dkzde.awb.fc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import su.dkzde.awb.fc.client.Board;
import su.dkzde.awb.fc.client.Pages;
import su.dkzde.awb.fc.client.Thread;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CachedAccessComponent implements CachedAccess {

    @Autowired
    private ReactiveAccess client;

    private record ThreadCache(Instant updated, Thread thread) {}

    private record BoardCache(ConcurrentHashMap<Long, ThreadCache> container) {}

    private final Map<Board, BoardCache> caches =
            Map.of(Board.vt, new BoardCache(new ConcurrentHashMap<>()));

    @Override
    public Mono<Thread> fetchThread(Board board, long op) {
        BoardCache cache = caches.get(board);
        ConcurrentHashMap<Long, ThreadCache> container = cache.container();
        return client.loadBoard(board).collectList()
                .flatMap(pages ->
                        Pages.lookupThread(pages, op).map(thread -> {
                            ThreadCache cached = container.get(op);
                            if (cached == null) {
                                return client.loadThread(board, op)
                                        .doOnNext(t -> container.putIfAbsent(op, new ThreadCache(Instant.now(), t)));
                            } else  if (cached.updated().isBefore(thread.modified())) {
                                return client.loadThread(board, op)
                                        .doOnNext(t -> container.put(op, new ThreadCache(Instant.now(), t)));
                            } else {
                                return Mono.just(cached.thread());
                            }
                        }).orElse(Mono.empty()));
    }

    @Scheduled(cron = "0 0 0 * * *")
    private void pruneTrigger() {
        for (Board board : Board.values()) {
            BoardCache target = caches.get(board);
            client.loadBoard(board).collectList()
                    .subscribe(pages -> {
                        ConcurrentHashMap<Long, ThreadCache> container = target.container();
                        for (long op : List.copyOf(container.keySet())) {
                            if (Pages.lookupThread(pages, op).isEmpty()) {
                                container.remove(op);
                            }
                        }
                    });
        }
    }
}
