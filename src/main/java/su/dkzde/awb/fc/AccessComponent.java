package su.dkzde.awb.fc;

import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;
import su.dkzde.awb.fc.client.Board;
import su.dkzde.awb.fc.client.Client;
import su.dkzde.awb.fc.client.Thread;
import su.dkzde.awb.fc.client.Threads;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.http.HttpClient;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class AccessComponent implements Access {

    private Client client;
    private ExecutorService pool;

    @PostConstruct
    private void postConstruct() {
        CustomizableThreadFactory factory = new CustomizableThreadFactory();
        factory.setThreadNamePrefix("4chan-client-pool-");
        factory.setDaemon(true);
        client = Client.create(HttpClient.newBuilder()
                .executor(pool = Executors.newCachedThreadPool(factory))
                .build());
    }

    @PreDestroy
    private void preDestroy() {
        pool.shutdownNow();
    }

    @Override
    public CompletableFuture<Threads> loadThreads(Board board) {
        return client.loadThreads(board);
    }

    @Override
    public CompletableFuture<Thread> loadThread(Board board, long op) {
        return client.loadThread(board, op);
    }
}
