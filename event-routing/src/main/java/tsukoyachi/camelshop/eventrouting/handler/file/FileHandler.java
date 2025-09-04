package tsukoyachi.camelshop.eventrouting.handler.file;

import org.apache.camel.Exchange;

public interface FileHandler {
    void process(Exchange exchange);
}
