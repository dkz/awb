package su.dkzde.awb.fc.client;

import java.util.List;
import java.util.Optional;

public final class Pages {
    private Pages() {}

    public static Optional<Page.Thread> lookupThread(List<Page> pages, long op) {
        for (Page page : pages) {
            for (Page.Thread thread : page.threads()) {
                if (op == thread.number()) {
                    return Optional.of(thread);
                }
            }
        }
        return Optional.empty();
    }
}
