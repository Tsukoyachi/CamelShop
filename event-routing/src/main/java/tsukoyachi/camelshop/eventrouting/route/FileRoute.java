package tsukoyachi.camelshop.eventrouting.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileRoute extends RouteBuilder {
    @Value("${camelshop.event-routing.input-directory}")
    private String inputDir;
    @Value("${camelshop.event-routing.processed-directory}")
    private String processedDir;
    @Value("${camelshop.event-routing.error-directory}")
    private String errorDir;

    @Override
    public void configure() throws Exception {
        onException(Exception.class)
                .handled(true)
                .log("Error during processing of ${header.CamelFileName}: ${exception.message}")
                .to(String.format("file:%s", errorDir))
                .end();

        from(String.format("file:%s?noop=false&delete=true", inputDir))
                .routeId("FileRoute")
                .log("Processing file: ${header.CamelFileName}")
                .choice()
                .when(header("CamelFileName").endsWith(".csv"))
                .log("This is a CSV file.")
                .when(header("CamelFileName").endsWith(".json"))
                .log("This is a JSON file.")
                .when(header("CamelFileName").endsWith(".xml"))
                .log("This is a XML file.")
                .otherwise()
                .throwException(new IllegalArgumentException("Unknown file type."))
                .end()
                .to(String.format("file:%s", processedDir));
    }
}
