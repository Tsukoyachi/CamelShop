package tsukoyachi.camelshop.eventrouting.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.CsvDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tsukoyachi.camelshop.eventrouting.handler.file.CsvFileHandler;

@Component
public class FileRoute extends RouteBuilder {
    @Value("${camelshop.event-routing.input-directory}")
    private String inputDir;
    @Value("${camelshop.event-routing.error-directory}")
    private String errorDir;

    @Override
    public void configure() throws Exception {
        onException(Exception.class)
                .handled(true)
                .log("Error during processing of ${header.CamelFileName}: ${exception.message}")
                .to(String.format("file:%s", errorDir))
                .end();

        from(String.format("file:%s?noop=true", inputDir))
                .routeId("FileRoute")
                .log("Processing file: ${header.CamelFileName}")
                .choice()
                .when(header("CamelFileName").endsWith(".csv"))
                    .to("direct:handleCsv")
                .when(header("CamelFileName").endsWith(".json"))
                    .to("direct:handleJson")
                .when(header("CamelFileName").endsWith(".xml"))
                    .to("direct:handleXml")
                .otherwise()
                    .throwException(new IllegalArgumentException("Unknown file type."))
                .end();

        from("direct:handleCsv")
            .log("This is a CSV file.")
                .unmarshal().csv()
                .bean(CsvFileHandler.class, "process")
                .end();

        from("direct:handleJson")
            .log("This is a JSON file.")
                .unmarshal().json(JsonLibrary.Jackson)
                .end();

        from("direct:handleXml")
            .log("This is a XML file.")

                .end();
    }
}
