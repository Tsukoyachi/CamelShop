package tsukoyachi.camelshop.eventrouting.handler.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;
import tsukoyachi.camelshop.common.models.SignupEvent;
import tsukoyachi.camelshop.common.models.OrderCreatedEvent;
import tsukoyachi.camelshop.common.models.PaymentProcessedEvent;
import tsukoyachi.camelshop.common.models.ShipmentDeliveredEvent;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Component
public class CsvFileHandler implements FileHandler {
    @Override
    public void process(Exchange exchange) {
        @SuppressWarnings("unchecked")
        List<List<String>> csv = (List<List<String>>) exchange.getIn().getBody();

        csv.stream()
           .filter(row -> !row.isEmpty())
           .forEach(row -> processEvent(row.getFirst(), row));
    }

    private void processEvent(String eventType, List<String> row) {
        log.info("Processing {} event: {}", eventType, row);

        switch (eventType) {
            case "signup" -> processSignup(row);
            case "order_created" -> processOrderCreated(row);
            case "payment_processed" -> processPaymentProcessed(row);
            case "shipment_delivered" -> processShipmentDelivered(row);
            default -> log.warn("Unknown event type: {}", eventType);
        }
    }

    private void validateRow(List<String> row, int expectedColumns, String eventType) {
        if (row == null || row.isEmpty()) {
            throw new IllegalArgumentException("Row must not be null or empty");
        }
        if (row.size() < expectedColumns) {
            throw new IllegalArgumentException(
                "Row must have at least %d columns for a %s event".formatted(expectedColumns, eventType)
            );
        }
    }

    private void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("%s must not be null or empty".formatted(fieldName));
        }
    }

    private Date parseDate(String dateString) {
        validateNotEmpty(dateString, "Date string");
        return Date.from(Instant.parse(dateString));
    }

    private void processSignup(List<String> row) {
        validateRow(row, 5, "signup");

        String userId = row.get(1);
        String username = row.get(2);
        String email = row.get(3);
        String dateString = row.get(4);

        validateNotEmpty(userId, "User ID");
        validateNotEmpty(username, "Username");
        validateNotEmpty(email, "Email");

        Date createdAt = parseDate(dateString);
        SignupEvent event = new SignupEvent(userId, username, email, createdAt);
        log.info("Signup event: {}", event);
    }

    private void processOrderCreated(List<String> row) {
        validateRow(row, 5, "order_created");

        String orderId = row.get(1);
        String userId = row.get(2);
        String cartString = row.get(3);
        String dateString = row.get(4);

        validateNotEmpty(orderId, "Order ID");
        validateNotEmpty(userId, "User ID");
        validateNotEmpty(cartString, "Cart string");

        Map<String, Integer> cart = parseCart(cartString);
        Date createdAt = parseDate(dateString);
        OrderCreatedEvent event = new OrderCreatedEvent(orderId, userId, cart, createdAt);
        log.info("Order created event: {}", event);
    }

    private void processPaymentProcessed(List<String> row) {
        validateRow(row, 5, "payment_processed");

        String orderId = row.get(1);
        String userId = row.get(2);
        String billingId = row.get(3);
        String dateString = row.get(4);

        validateNotEmpty(orderId, "Order ID");
        validateNotEmpty(userId, "User ID");
        validateNotEmpty(billingId, "Billing ID");

        Date processedAt = parseDate(dateString);
        PaymentProcessedEvent event = new PaymentProcessedEvent(orderId, userId, billingId, processedAt);
        log.info("Payment processed event: {}", event);
    }

    private void processShipmentDelivered(List<String> row) {
        validateRow(row, 5, "shipment_delivered");

        String userId = row.get(1);
        String orderId = row.get(2);
        String parcelId = row.get(3);
        String dateString = row.get(4);

        validateNotEmpty(userId, "User ID");
        validateNotEmpty(orderId, "Order ID");
        validateNotEmpty(parcelId, "Parcel ID");

        Date deliveredAt = parseDate(dateString);
        ShipmentDeliveredEvent event = new ShipmentDeliveredEvent(userId, orderId, parcelId, deliveredAt);
        log.info("Shipment delivered event: {}", event);
    }

    private Map<String, Integer> parseCart(String cartString) {
        Map<String, Integer> cart = new HashMap<>();

        for (String item : cartString.split("\\|")) {
            String[] parts = item.split(":");
            if (parts.length == 2) {
                cart.put(parts[0].trim(), Integer.parseInt(parts[1].trim()));
            }
        }

        return cart;
    }
}
