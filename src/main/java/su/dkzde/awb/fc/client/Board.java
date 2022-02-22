package su.dkzde.awb.fc.client;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

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
    };

    public abstract URI boardAPI();
    public abstract URI threadAPI(long op);
}
