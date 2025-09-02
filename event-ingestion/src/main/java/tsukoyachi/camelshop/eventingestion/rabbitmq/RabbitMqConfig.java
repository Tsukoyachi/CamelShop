package tsukoyachi.camelshop.eventingestion.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    static final String EXCHANGE_NAME = "eventingestion.exchange";
    static final String QUEUE_NAME = "eventingestion.queue";

    @Bean(EXCHANGE_NAME)
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean(QUEUE_NAME)
    public Queue queue() {
        return new Queue(QUEUE_NAME, false);
    }

    @Bean
    public Binding binding(TopicExchange exchange, Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with("foo.bar.#");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
