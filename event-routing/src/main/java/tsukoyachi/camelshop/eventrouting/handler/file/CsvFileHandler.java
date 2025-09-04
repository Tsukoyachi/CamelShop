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
        List<List<String>> csv = (List<List<String>>) exchange.getIn().getBody();
        for (List<String> row : csv) {
            if (row.isEmpty()) {
                continue;
            }

            String eventType = row.getFirst();
            processEvent(eventType, row);
        }
    }

    private void processEvent(String eventType, List<String> row) {
        switch (eventType) {
            case "signup":
                log.info("Processing signup event: {}", row);
                processSignup(row);
                break;
            case "order_created":
                log.info("Processing order_created event: {}", row);
                processOrderCreated(row);
                break;
            case "payment_processed":
                log.info("Processing payment_processed event: {}", row);
                processPaymentProcessed(row);
                break;
            case "shipment_delivered":
                log.info("Processing shipment_delivered event: {}", row);
                processShipmentDelivered(row);
                break;
            default:
                log.warn("Unknown event type: {}", eventType);
                break;
        }
    }

    private void validateRow(List<String> row, int expectedColumns, String eventType) {
        if (row == null || row.isEmpty()) {
            throw new IllegalArgumentException("Row must not be null or empty");
        }
        if (row.size() < expectedColumns) {
            throw new IllegalArgumentException(String.format("Row must have at least %d columns for a %s event", expectedColumns, eventType));
        }
    }

    private void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(String.format("%s must not be null or empty", fieldName));
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
        String shipmentId = row.get(3);
        String dateString = row.get(4);

        validateNotEmpty(userId, "User ID");
        validateNotEmpty(orderId, "Order ID");
        validateNotEmpty(shipmentId, "Shipment ID");

        Date deliveredAt = parseDate(dateString);
        ShipmentDeliveredEvent event = new ShipmentDeliveredEvent(userId, orderId, shipmentId, deliveredAt);
        log.info("Shipment delivered event: {}", event);
    }

    private Map<String, Integer> parseCart(String cartString) {
        Map<String, Integer> cart = new HashMap<>();
        String[] items = cartString.split("\\|");
        for (String item : items) {
            String[] parts = item.split(":");
            if (parts.length == 2) {
                cart.put(parts[0].trim(), Integer.parseInt(parts[1].trim()));
            }
        }
        return cart;
    }
}
