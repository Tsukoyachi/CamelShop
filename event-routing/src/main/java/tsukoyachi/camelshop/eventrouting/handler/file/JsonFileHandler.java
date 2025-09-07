package tsukoyachi.camelshop.eventrouting.handler.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;
import tsukoyachi.camelshop.common.models.OrderCreatedEvent;
import tsukoyachi.camelshop.common.models.PaymentProcessedEvent;
import tsukoyachi.camelshop.common.models.ShipmentDeliveredEvent;
import tsukoyachi.camelshop.common.models.SignupEvent;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class JsonFileHandler implements FileHandler {
    @Override
    @SuppressWarnings("unchecked")
    public void process(Exchange exchange) {
        if (exchange == null || exchange.getIn() == null || exchange.getIn().getBody() == null) {
            throw new IllegalArgumentException("Exchange and its input message must not be null");
        }

        Object body = exchange.getIn().getBody();

        switch (body) {
            case List<?> list -> {
                log.info("Processing JSON list of size {}", list.size());
                processJsonList(list);
            }
            case Map<?, ?> map -> {
                log.info("Processing single JSON object");
                processJsonObject((Map<String, Object>) map);
            }
            default -> throw new IllegalArgumentException(
                    "Unsupported body type: %s".formatted(body.getClass().getName())
            );
        }
    }

    @SuppressWarnings("unchecked")
    private void processJsonList(List<?> jsonList) {
        jsonList.stream()
                .filter(Map.class::isInstance)
                .map(item -> (Map<String, Object>) item)
                .forEach(this::processJsonObject);
    }

    private void processJsonObject(Map<String, Object> jsonObject) {
        String eventType = (String) jsonObject.get("eventType");
        if (eventType == null || eventType.isEmpty()) {
            log.warn("Missing or empty eventType in JSON object: {}", jsonObject);
            return;
        }

        log.info("Processing {} event from JSON: {}", eventType, jsonObject);

        switch (eventType) {
            case "signup" -> processSignupFromJson(jsonObject);
            case "order_created" -> processOrderCreatedFromJson(jsonObject);
            case "payment_processed" -> processPaymentProcessedFromJson(jsonObject);
            case "shipment_delivered" -> processShipmentDeliveredFromJson(jsonObject);
            default -> log.warn("Unknown event type: {}", eventType);
        }
    }

    private void validateJsonField(Map<String, Object> json, String fieldName) {
        Object value = json.get(fieldName);
        if (value == null || (value instanceof String str && str.isEmpty())) {
            throw new IllegalArgumentException("%s must not be null or empty".formatted(fieldName));
        }
    }

    private String getStringValue(Map<String, Object> json, String fieldName) {
        validateJsonField(json, fieldName);
        return (String) json.get(fieldName);
    }

    private Date parseDateFromJson(Map<String, Object> json, String fieldName) {
        String dateString = getStringValue(json, fieldName);
        return Date.from(Instant.parse(dateString));
    }

    private void processSignupFromJson(Map<String, Object> json) {
        String userId = getStringValue(json, "userId");
        String username = getStringValue(json, "username");
        String email = getStringValue(json, "email");
        Date createdAt = parseDateFromJson(json, "createdAt");

        SignupEvent event = new SignupEvent(userId, username, email, createdAt);
        log.info("Signup event: {}", event);
    }

    private void processOrderCreatedFromJson(Map<String, Object> json) {
        String orderId = getStringValue(json, "orderId");
        String userId = getStringValue(json, "userId");
        Date createdAt = parseDateFromJson(json, "createdAt");
        Map<String, Integer> cart = parseCartFromJson(json);

        OrderCreatedEvent event = new OrderCreatedEvent(orderId, userId, cart, createdAt);
        log.info("Order created event: {}", event);
    }

    private void processPaymentProcessedFromJson(Map<String, Object> json) {
        String orderId = getStringValue(json, "orderId");
        String userId = getStringValue(json, "userId");
        String billingId = getStringValue(json, "billId");
        Date processedAt = parseDateFromJson(json, "processedAt");

        PaymentProcessedEvent event = new PaymentProcessedEvent(orderId, userId, billingId, processedAt);
        log.info("Payment processed event: {}", event);
    }

    private void processShipmentDeliveredFromJson(Map<String, Object> json) {
        String userId = getStringValue(json, "userId");
        String orderId = getStringValue(json, "orderId");
        String parcelId = getStringValue(json, "parcelId");
        Date deliveredAt = parseDateFromJson(json, "deliveredAt");

        ShipmentDeliveredEvent event = new ShipmentDeliveredEvent(userId, orderId, parcelId, deliveredAt);
        log.info("Shipment delivered event: {}", event);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Integer> parseCartFromJson(Map<String, Object> json) {
        Map<String, Integer> cart = new HashMap<>();
        Object itemsObj = json.get("items");

        switch (itemsObj) {
            case List<?> items -> items.stream()
                    .filter(Map.class::isInstance)
                    .map(item -> (Map<String, Object>) item)
                    .forEach(itemData -> {
                        String itemId = (String) itemData.get("itemId");
                        Object quantityObj = itemData.get("quantity");

                        if (itemId != null && quantityObj != null) {
                            Integer quantity = switch (quantityObj) {
                                case Integer i -> i;
                                case Object obj -> Integer.parseInt(obj.toString());
                            };
                            cart.put(itemId, quantity);
                        }
                    });

            case Map<?, ?> itemsMap -> {
                Map<String, Object> items = (Map<String, Object>) itemsMap;
                items.forEach((key, value) -> {
                    Integer quantity = switch (value) {
                        case Integer i -> i;
                        case Object obj -> Integer.parseInt(obj.toString());
                    };
                    cart.put(key, quantity);
                });
            }
            default -> { /* No items found */ }
        }

        return cart;
    }
}
