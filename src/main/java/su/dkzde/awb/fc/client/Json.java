package su.dkzde.awb.fc.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 *
 */
public class Json {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Page {
        public @JsonProperty("page") int number;
        public List<Thread> threads;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Thread {
        public @JsonProperty("no") long number;
        public @JsonProperty("last_modified") long modifiedTs;
        public int replies;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ThreadPosts {
        public List<Post> posts;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Post {

        public @JsonProperty("no") long number;
        public long time;
        public String name;
        public @JsonProperty("capcode") CaptionCode caption;
        public @JsonProperty("com") String comment;
        public @JsonProperty("resto") Integer reply;

        /*
         * Properties that are applied only to original post.
         * Due to oddity in json api some integer properties represent a boolean value,
         * they are prefixed with 'is' and are either 1 or not present in the response.
         */
        public @JsonProperty("sub") String subject;
        public @JsonProperty("sticky") Integer isThreadSticky;
        public @JsonProperty("closed") Integer isThreadClosed;
        public @JsonProperty("replies") Integer threadReplyCount;
        public @JsonProperty("images") Integer threadImageCount;
        public @JsonProperty("bumplimit") Integer isBumpLimitReached;
        public @JsonProperty("imagelimit") Integer isImageLimitReached;
        public @JsonProperty("unique_ips")Integer uniqueIps;
        public @JsonProperty("archived") Integer isThreadArchived;
        public @JsonProperty("archived_on") Long threadArchivedTs;

        /*
         * Properties that are present only if post has an attachment.
         * Attachment however can be already deleted by board moderator,
         * in that case isAttachmentDeleted is set to 1.
         */
        public @JsonProperty("filedeleted") Integer isAttachmentDeleted;
        public @JsonProperty("filename") String attachmentFilename;
        public @JsonProperty("ext") String attachmentExtension;
        public @JsonProperty("fsize") Integer attachmentSize;
        public @JsonProperty("spoiler") Integer isSpoiler;
        public String md5;
    }
}
