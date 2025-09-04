package tsukoyachi.camelshop.eventingestion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import tsukoyachi.camelshop.common.config.SqliteConfig;

@SpringBootApplication
@Import({SqliteConfig.class})
public class EventIngestionApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventIngestionApplication.class, args);
    }

}
