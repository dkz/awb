package su.dkzde.awb;

import org.javacord.api.DiscordApi;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class AwbApplicationTests {

    @MockBean
    public DiscordApi discord;

    @Test
    void contextLoads() {
    }
}
