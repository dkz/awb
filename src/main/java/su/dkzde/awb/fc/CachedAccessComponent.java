package su.dkzde.awb.fc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.dkzde.awb.fc.client.Thread;
import su.dkzde.awb.fc.client.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CachedAccessComponent implements CachedAccess {

    @Autowired
    private ReactiveAccess client;

    private record ThreadCache(Instant updated, Thread thread) {}

    private record BoardCache(ConcurrentHashMap<Long, ThreadCache> container) {}

    private final Map<Board, BoardCache> caches = Map.of(Board.vt, new BoardCache(new ConcurrentHashMap<>()));
    private final ArrayList<CacheEventListener> listeners = new ArrayList<>();

    @Override
    public Mono<Thread> fetchThread(Board board, long op) {
        BoardCache cache = caches.get(board);
        ConcurrentHashMap<Long, ThreadCache> container = cache.container();
        return client.loadBoard(board).collectList()
                .flatMap(pages ->
                        Pages.lookupThread(pages, op).map(thread -> {
                            ThreadCache cached = container.get(op);
                            if (cached == null) {
                                return client.loadThread(board, op).doOnNext(t -> update(cache, t));
                            } else if (cached.updated().isBefore(thread.modified())) {
                                return client.loadThread(board, op).doOnNext(t -> update(cache, t));
                            } else {
                                return Mono.just(cached.thread());
                            }
                        }).orElseGet(() -> {
                            ThreadCache cached = container.get(op);
                            if (cached != null) {
                                return Mono.just(cached.thread());
                            } else {
                                return Mono.empty();
                            }
                        }));
    }

    @Override
    public void addListener(CacheEventListener listener) {
        listeners.add(listener);
    }

    private void update(BoardCache cache, Thread thread) {
        ThreadCache update = new ThreadCache(Instant.now(), thread);
        while (true) {
            ThreadCache cached = cache.container.get(thread.number());
            if (cached == null) {
                if (null == cache.container.putIfAbsent(thread.number(), update)) {
                    return;
                }
            } else if (cached.updated().isAfter(update.updated())) {
                return;
            } else {
                if (cache.container.replace(thread.number(), cached, update)) {
                    Thread before = cached.thread();
                    Thread after = update.thread();
                    onUpdate(before, after);
                    return;
                }
            }
        }
    }

    private void onUpdate(Thread before, Thread after) {
        Map<Post, ArrayList<Post>> interactions = Threads.interactions(after);
        interactions.forEach((post, quoted) -> {
            int quotedNew = 0;
            int quotedTotal = 0;
            for (Post quote : quoted) {
                quotedTotal++;
                if (before.post(quote.number()).isEmpty()) {
                    quotedNew++;
                }
            }
            if (quotedNew > 0) {
                for (CacheEventListener listener : listeners) {
                    listener.emitPostQuotesEvent(after, post, quotedTotal, quotedNew);
                }
            }
        });
    }

    @Scheduled(cron = "0 * * * * *")
    private void updateTrigger() {
        for (Board board : Board.values()) {
            BoardCache cache = caches.get(board);
            ConcurrentHashMap<Long, ThreadCache> container = cache.container();
            client.loadBoard(board)
                    .flatMap(page -> {
                        Flux<Thread> threads = Flux.empty();
                        for (Page.Thread thread : page.threads()) {
                            ThreadCache cached = container.get(thread.number());
                            if (cached == null || cached.updated().isBefore(thread.modified())) {
                                threads = Flux.merge(threads,
                                        client.loadThread(board, thread.number()));
                            }
                        }
                        return threads;
                    })
                    .doOnNext(t -> update(cache, t))
                    .subscribe();
        }
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
