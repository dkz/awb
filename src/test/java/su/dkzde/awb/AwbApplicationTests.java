package su.dkzde.awb;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.javacord.api.DiscordApi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import su.dkzde.awb.fc.client.Board;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class AwbApplicationTests {

    @MockBean
    public DiscordApi discord;

    @Autowired
    public ObjectMapper mapper;

    private record SubscriptionSettings(boolean popularPosts, int popularPostThreshold) {}

    private static final class Json {
        @JsonProperty
        Map<Board, Map<String, SubscriptionSettings>> subscriptions = new HashMap<>();
    }

    @Test
    void contextLoads() throws JsonProcessingException {
        Json j = new Json();
        HashMap<String, SubscriptionSettings> s = new HashMap<>();
        j.subscriptions.put(Board.vt, s);
        s.put("t", new SubscriptionSettings(true, 0));

        System.out.println(mapper.writeValueAsString(j));
    }
}
