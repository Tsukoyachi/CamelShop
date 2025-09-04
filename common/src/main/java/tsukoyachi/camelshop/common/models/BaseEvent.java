package tsukoyachi.camelshop.common.models;

public record BaseEvent(
        String type,
        String payload
) {
}
