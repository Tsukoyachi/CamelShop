package tsukoyachi.camelshop.eventrouting.handler.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import tsukoyachi.camelshop.common.models.SignupEvent;
import tsukoyachi.camelshop.common.models.OrderCreatedEvent;
import tsukoyachi.camelshop.common.models.PaymentProcessedEvent;
import tsukoyachi.camelshop.common.models.ShipmentDeliveredEvent;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Component
public class XmlFileHandler implements FileHandler {
    @Override
    public void process(Exchange exchange) {
        if (exchange == null || exchange.getIn() == null || exchange.getIn().getBody() == null) {
            throw new IllegalArgumentException("Exchange and its input message must not be null");
        }

        Document document = exchange.getIn().getBody(Document.class);
        if (document == null) {
            throw new IllegalArgumentException("Body must be a valid XML Document");
        }

        NodeList eventNodes = document.getElementsByTagName("event");
        log.info("Processing XML with {} event(s)", eventNodes.getLength());

        for (int i = 0; i < eventNodes.getLength(); i++) {
            Element eventElement = (Element) eventNodes.item(i);
            processXmlEvent(eventElement);
        }
    }

    private void processXmlEvent(Element eventElement) {
        String eventType = getElementText(eventElement, "eventType");
        if (eventType == null || eventType.isEmpty()) {
            log.warn("Missing or empty eventType in XML event: {}", eventElement);
            return;
        }

        log.info("Processing {} event from XML", eventType);

        switch (eventType) {
            case "signup" -> processSignupFromXml(eventElement);
            case "order_created" -> processOrderCreatedFromXml(eventElement);
            case "payment_processed" -> processPaymentProcessedFromXml(eventElement);
            case "shipment_delivered" -> processShipmentDeliveredFromXml(eventElement);
            default -> log.warn("Unknown event type: {}", eventType);
        }
    }

    private void validateXmlField(Element element, String fieldName) {
        String value = getElementText(element, fieldName);
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("%s must not be null or empty".formatted(fieldName));
        }
    }

    private String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent().trim();
        }
        return null;
    }

    private Date parseDateFromXml(Element element, String fieldName) {
        String dateString = getElementText(element, fieldName);
        if (dateString == null || dateString.isEmpty()) {
            throw new IllegalArgumentException("%s must not be null or empty".formatted(fieldName));
        }
        return Date.from(Instant.parse(dateString));
    }

    private void processSignupFromXml(Element element) {
        String userId = getElementText(element, "userId");
        String username = getElementText(element, "username");
        String email = getElementText(element, "email");
        Date createdAt = parseDateFromXml(element, "createdAt");

        validateXmlField(element, "userId");
        validateXmlField(element, "username");
        validateXmlField(element, "email");

        SignupEvent event = new SignupEvent(userId, username, email, createdAt);
        log.info("Signup event: {}", event);
    }

    private void processOrderCreatedFromXml(Element element) {
        String orderId = getElementText(element, "orderId");
        String userId = getElementText(element, "userId");
        Date createdAt = parseDateFromXml(element, "createdAt");

        validateXmlField(element, "orderId");
        validateXmlField(element, "userId");

        Map<String, Integer> cart = parseCartFromXml(element);

        OrderCreatedEvent event = new OrderCreatedEvent(orderId, userId, cart, createdAt);
        log.info("Order created event: {}", event);
    }

    private void processPaymentProcessedFromXml(Element element) {
        String orderId = getElementText(element, "orderId");
        String userId = getElementText(element, "userId");
        String billingId = getElementText(element, "billId"); // Note: XML uses "billId" not "billId"
        Date processedAt = parseDateFromXml(element, "createdAt"); // XML uses "createdAt" for all timestamps

        validateXmlField(element, "orderId");
        validateXmlField(element, "userId");
        validateXmlField(element, "billId");

        PaymentProcessedEvent event = new PaymentProcessedEvent(orderId, userId, billingId, processedAt);
        log.info("Payment processed event: {}", event);
    }

    private void processShipmentDeliveredFromXml(Element element) {
        String userId = getElementText(element, "userId");
        String orderId = getElementText(element, "orderId");
        String parcelId = getElementText(element, "parcelId"); // Note: XML uses "parcelId" not "parcelId"
        Date deliveredAt = parseDateFromXml(element, "createdAt"); // XML uses "createdAt" for all timestamps

        validateXmlField(element, "userId");
        validateXmlField(element, "orderId");
        validateXmlField(element, "parcelId");

        ShipmentDeliveredEvent event = new ShipmentDeliveredEvent(userId, orderId, parcelId, deliveredAt);
        log.info("Shipment delivered event: {}", event);
    }

    private Map<String, Integer> parseCartFromXml(Element element) {
        Map<String, Integer> cart = new HashMap<>();

        NodeList itemsNodes = element.getElementsByTagName("items");
        if (itemsNodes.getLength() > 0) {
            Element itemsElement = (Element) itemsNodes.item(0);
            NodeList itemNodes = itemsElement.getElementsByTagName("item");

            for (int i = 0; i < itemNodes.getLength(); i++) {
                Element itemElement = (Element) itemNodes.item(i);
                String itemId = getElementText(itemElement, "itemId");
                String quantityStr = getElementText(itemElement, "quantity");

                if (itemId != null && quantityStr != null) {
                    try {
                        Integer quantity = Integer.parseInt(quantityStr);
                        cart.put(itemId, quantity);
                    } catch (NumberFormatException e) {
                        log.warn("Invalid quantity value '{}' for item '{}'", quantityStr, itemId);
                    }
                }
            }
        }

        return cart;
    }
}
