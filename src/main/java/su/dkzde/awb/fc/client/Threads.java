package su.dkzde.awb.fc.client;

import java.util.*;
import java.util.function.Function;

public final class Threads {
    private Threads() {}

    public static Map<Post, ArrayList<Post>> interactions(Thread thread) {

        LinkedList<Post> posts = thread.listPosts();
        Map<Post, ArrayList<Post>> interactions = new HashMap<>(2 * posts.size());
        Function<Post, ArrayList<Post>> newLinkedList = _p -> new ArrayList<>();

        Iterator<Post> it = posts.descendingIterator();
        while (it.hasNext()) {
            Post post = it.next();
            post.comment().ifPresent(comment -> {
                for (long quote : comment.quotes()) {
                    thread.post(quote).ifPresent(quoted -> {
                        interactions.computeIfAbsent(quoted, newLinkedList).add(post);
                    });
                }
            });
        }

        return interactions;
    }
}
