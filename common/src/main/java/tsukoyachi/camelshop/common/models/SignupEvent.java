package tsukoyachi.camelshop.common.models;

import java.util.Date;

public record SignupEvent(
        String userId,
        String email,
        String username,
        Date createdAt
) {
}
