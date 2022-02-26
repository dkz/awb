package su.dkzde.awb;

import su.dkzde.awb.fc.client.Board;

public interface Permissions {

    boolean embedsPermitted(String channel, Board board);
    void setEmbedsPermitted(String channel, Board board, boolean permitted);
}
