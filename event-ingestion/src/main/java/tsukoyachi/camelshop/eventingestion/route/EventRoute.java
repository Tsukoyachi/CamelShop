package tsukoyachi.camelshop.eventingestion.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EventRoute extends RouteBuilder {
    @Value("${camelshop.rabbitmq.queue}")
    private String queueName;
    @Value("${camelshop.rabbitmq.exchange}")
    private String exchangeName;

    @Override
    public void configure() throws Exception {
        // Some test for camel setup with rabbitmq instead of classic rabbitmq listener
        from("spring-rabbitmq:" + exchangeName + "?queues=" + queueName)
            .log("Received message from RabbitMQ: ${body}")
            .choice()
                .when(body().contains("Camel"))
                    .log("Message is true")
                .otherwise()
                    .log("Message is not true");
    }
}
