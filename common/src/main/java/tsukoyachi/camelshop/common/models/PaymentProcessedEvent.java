package tsukoyachi.camelshop.common.models;

import java.util.Date;

public record PaymentProcessedEvent(
        String orderId,
        String userId,
        String billingId,
        Date processedAt
) {
}
