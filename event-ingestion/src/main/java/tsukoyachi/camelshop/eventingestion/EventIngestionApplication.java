package tsukoyachi.camelshop.eventingestion;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class EventIngestionApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventIngestionApplication.class, args);
    }

}
