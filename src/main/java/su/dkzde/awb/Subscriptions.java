package su.dkzde.awb;

import su.dkzde.awb.fc.client.Board;

public interface Subscriptions {

    void subscribePopularPosts(String channel, Board board, int threshold);
    void unsubscribePopularPosts(String channel, Board board);
}
