package tsukoyachi.camelshop.common.models;

import java.util.Date;
import java.util.Map;

public record OrderCreatedEvent(
        String orderId,
        String userId,
        Map<String, Integer> cart,
        Date createdAt
) {
}
