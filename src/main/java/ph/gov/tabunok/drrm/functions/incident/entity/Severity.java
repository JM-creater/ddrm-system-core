package ph.gov.tabunok.drrm.functions.incident.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Severity implements Labeled {
    LOW("Low"),
    MODERATE("Moderate"),
    HIGH("High / Critical");

    private final String label;
}
