package su.dkzde.awb;

import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;

@Component
public class Dispatcher {

    @Autowired
    private DiscordApi api;

    @Autowired
    private ApplicationContext context;

    private List<DispatchController> chain;

    @PostConstruct
    private void postConstruct() {
        chain = context.getBeansOfType(DispatchController.class)
                .values()
                .stream()
                .sorted(Comparator.comparing(bean -> {
                    Class<?> type = bean.getClass();
                    DispatcherPriority priority = type.getDeclaredAnnotation(DispatcherPriority.class);
                    if (priority != null) {
                        return priority.value();
                    } else {
                        return 0;
                    }
                }))
                .toList();
        api.addMessageCreateListener(this::onMessageCreate);
    }

    private void onMessageCreate(MessageCreateEvent event) {
        for (DispatchController controller : chain) {
            if (controller.consumeMessageEvent(event)) {
                return;
            }
        }
    }
}
