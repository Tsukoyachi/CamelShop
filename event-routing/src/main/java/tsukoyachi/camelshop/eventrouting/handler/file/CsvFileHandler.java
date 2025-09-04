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
                // Handle signup event
                log.info("Processing signup event: {}", row);
                processSignup(row);
                break;
            case "order_created":
                // Handle order_created event
                log.info("Processing order_created event: {}", row);
                processOrderCreated(row);
                break;
            case "payment_processed":
                // Handle payment_processed event
                log.info("Processing payment_processed event: {}", row);
                processPaymentProcessed(row);
                break;
            case "shipment_delivered":
                // Handle shipment_delivered event
                log.info("Processing shipment_delivered event: {}", row);
                processShipmentDelivered(row);
                break;
            default:
                log.warn("Unknown event type: {}", eventType);
                break;
        }
    }

    private void processSignup(List<String> row) {
        if (row == null || row.isEmpty()) {
            throw new IllegalArgumentException("Row must not be null or empty");
        }

        if (row.size() < 5) {
            throw new IllegalArgumentException("Row must have at least 5 columns for a signup event");
        }

        String userId = row.get(1);
        String username = row.get(2);
        String email = row.get(3);
        String dateString = row.get(4);

        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID must not be null or empty");
        }
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username must not be null or empty");
        }
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email must not be null or empty");
        }
        if (dateString == null || dateString.isEmpty()) {
            throw new IllegalArgumentException("Date string must not be null or empty");
        }

        Instant instant = Instant.parse(dateString);
        SignupEvent event = new SignupEvent(userId, username, email, Date.from(instant));
        log.info("Signup event: {}", event);
    }

    private void processOrderCreated(List<String> row) {
        if (row == null || row.isEmpty()) {
            throw new IllegalArgumentException("Row must not be null or empty");
        }

        if (row.size() < 5) {
            throw new IllegalArgumentException("Row must have at least 5 columns for an order_created event");
        }

        String orderId = row.get(1);
        String userId = row.get(2);
        String cartString = row.get(3); // Wanted format: "item1:qty1|item2:qty2"
        String dateString = row.get(4);

        if (orderId == null || orderId.isEmpty()) {
            throw new IllegalArgumentException("Order ID must not be null or empty");
        }
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID must not be null or empty");
        }
        if (cartString == null || cartString.isEmpty()) {
            throw new IllegalArgumentException("Cart string must not be null or empty");
        }
        if (dateString == null || dateString.isEmpty()) {
            throw new IllegalArgumentException("Date string must not be null or empty");
        }

        // Parse cart string into Map
        Map<String, Integer> cart = new HashMap<>();
        String[] items = cartString.split("\\|");
        for (String item : items) {
            String[] parts = item.split(":");
            if (parts.length == 2) {
                cart.put(parts[0].trim(), Integer.parseInt(parts[1].trim()));
            }
        }

        Instant instant = Instant.parse(dateString);
        OrderCreatedEvent event = new OrderCreatedEvent(orderId, userId, cart, Date.from(instant));
        log.info("Processing order_created event: {}", event);
    }

    private void processPaymentProcessed(List<String> row) {
        if (row == null || row.isEmpty()) {
            throw new IllegalArgumentException("Row must not be null or empty");
        }

        if (row.size() < 5) {
            throw new IllegalArgumentException("Row must have at least 5 columns for a payment_processed event");
        }

        String orderId = row.get(1);
        String userId = row.get(2);
        String billingId = row.get(3);
        String dateString = row.get(4);

        if (orderId == null || orderId.isEmpty()) {
            throw new IllegalArgumentException("Order ID must not be null or empty");
        }
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID must not be null or empty");
        }
        if (billingId == null || billingId.isEmpty()) {
            throw new IllegalArgumentException("Billing ID must not be null or empty");
        }
        if (dateString == null || dateString.isEmpty()) {
            throw new IllegalArgumentException("Date string must not be null or empty");
        }

        Instant instant = Instant.parse(dateString);
        PaymentProcessedEvent event = new PaymentProcessedEvent(orderId, userId, billingId, Date.from(instant));
        log.info("Processing payment_processed event: {}", event);
    }

    private void processShipmentDelivered(List<String> row) {
        if (row == null || row.isEmpty()) {
            throw new IllegalArgumentException("Row must not be null or empty");
        }

        if (row.size() < 5) {
            throw new IllegalArgumentException("Row must have at least 5 columns for a shipment_delivered event");
        }

        String userId = row.get(1);
        String orderId = row.get(2);
        String shipmentId = row.get(3);
        String dateString = row.get(4);

        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID must not be null or empty");
        }
        if (orderId == null || orderId.isEmpty()) {
            throw new IllegalArgumentException("Order ID must not be null or empty");
        }
        if (shipmentId == null || shipmentId.isEmpty()) {
            throw new IllegalArgumentException("Shipment ID must not be null or empty");
        }
        if (dateString == null || dateString.isEmpty()) {
            throw new IllegalArgumentException("Date string must not be null or empty");
        }

        Instant instant = Instant.parse(dateString);
        ShipmentDeliveredEvent event = new ShipmentDeliveredEvent(userId, orderId, shipmentId, Date.from(instant));
        log.info("Processing shipment_delivered event: {}", event);
    }
}
