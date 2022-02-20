package su.dkzde.awb.fc.client;

import java.net.URI;

public enum Board {

    vt {
        private final URI threads = URI.create("https://a.4cdn.org/vt/threads.json");
        @Override public URI threadsAPI() {
            return threads;
        }
        @Override public URI threadAPI(long op) {
            return URI.create("https://a.4cdn.org/vt/thread/" + op + ".json");
        }
        @Override public URI thread(long op) {
            return URI.create("https://boards.4chan.org/vt/thread/" + op);
        }
        @Override public URI post(long thread, long post) {
            return URI.create("https://boards.4chan.org/vt/thread/" + thread + "#p" + post);
        }
    };

    public abstract URI threadsAPI();
    public abstract URI threadAPI(long op);
    public abstract URI thread(long op);
    public abstract URI post(long thread, long post);
}
