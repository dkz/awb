package su.dkzde.awb.fc.client;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.regex.Pattern;

public enum Board {

    vt {
        @Override
        public URI boardAPI() {
            return URI.create("https://a.4cdn.org/vt/threads.json");
        }
        @Override
        public URI threadAPI(long op) {
            return UriComponentsBuilder.fromHttpUrl("https://a.4cdn.org/vt/thread/{thread}.json")
                    .buildAndExpand(op)
                    .toUri();
        }
        @Override
        public Pattern pattern() {
            return Pattern.compile("\\bhttps://boards.4chan.org/vt/thread/(?<thread>\\d+)(?:#p(?<post>\\d+))?\\b");
        }
    };

    public abstract URI boardAPI();
    public abstract URI threadAPI(long op);
    public abstract Pattern pattern();
}
