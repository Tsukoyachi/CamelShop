package tsukoyachi.camelshop.common.models;

import java.util.Date;

public record ShipmentDeliveredEvent(
        String userId,
        String orderId,
        String shipmentId,
        Date deliveredAt
) {
}
