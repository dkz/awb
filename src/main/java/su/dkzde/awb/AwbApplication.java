package su.dkzde.awb;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class AwbApplication {

    @Autowired
    private Environment env;

    public static void main(String[] args) {
        SpringApplication.run(AwbApplication.class, args);
    }

    @Bean
    @ConfigurationProperties("discord")
    public DiscordApi discord() {
        return new DiscordApiBuilder()
                .setToken(env.getProperty("discord.token"))
                .setAllNonPrivilegedIntents()
                .login()
                .join();
    }

    @Bean
    public WebClient web() {
        return WebClient.create();
    }
}
