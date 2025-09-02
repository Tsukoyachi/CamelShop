package tsukoyachi.camelshop.eventingestion.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tsukoyachi.camelshop.eventingestion.business.BusinessService;
import tsukoyachi.camelshop.eventingestion.dto.EventMessage;

@Component
public class Receiver {
    private final BusinessService businessService;

    public Receiver(BusinessService businessService) {
        this.businessService = businessService;
    }

    @RabbitListener(queues = "eventingestion.queue")
    public void receiveMessage(EventMessage eventMessage) {
        businessService.processMessage(eventMessage.getMessage());
    }

}