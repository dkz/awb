package su.dkzde.awb.fc;

import su.dkzde.awb.fc.client.Post;
import su.dkzde.awb.fc.client.Thread;

public interface CacheEventListener {

    void emitPostQuotesEvent(
            Thread thread,
            Post post,
            int quotesTotal,
            int quotesNew);
}
