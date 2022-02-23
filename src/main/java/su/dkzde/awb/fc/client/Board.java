package su.dkzde.awb.fc.client;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.regex.Pattern;

public enum Board {

    vt {
        @Override
        public Pattern pattern() {
            return Pattern.compile("\\bhttps://boards.(4channel|4chan).org/vt/thread/(?<thread>\\d+)(?:#p(?<post>\\d+))?\\b");
        }
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
        public URI attachment(long id, String ext) {
            return UriComponentsBuilder.fromHttpUrl("https://i.4cdn.org/vt/{id}{ext}")
                    .buildAndExpand(id, ext)
                    .toUri();
        }
        @Override
        public URI thumbnail(long id) {
            return UriComponentsBuilder.fromHttpUrl("https://i.4cdn.org/vt/{id}s.jpg")
                    .buildAndExpand(id)
                    .toUri();
        }
        @Override
        public URI thread(long op) {
            return UriComponentsBuilder.fromHttpUrl("https://boards.4channel.org/vt/thread/{thread}")
                    .buildAndExpand(op)
                    .toUri();
        }
        @Override
        public URI post(long op, long number) {
            return UriComponentsBuilder.fromHttpUrl("https://boards.4channel.org/vt/thread/{thread}#p{post}")
                    .buildAndExpand(op, number)
                    .toUri();
        }
    };

    public abstract URI boardAPI();
    public abstract URI threadAPI(long op);
    public abstract URI attachment(long id, String ext);
    public abstract URI thumbnail(long id);
    public abstract URI thread(long op);
    public abstract URI post(long op, long number);
    public abstract Pattern pattern();
}
