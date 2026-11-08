package ph.gov.tabunok.drrm.functions.incident.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IncidentStatus implements Labeled {
    PENDING("Pending"),
    VERIFIED("Verified"),
    RESPONDING("Responding"),
    RESOLVED("Resolved"),
    REJECTED("Rejected");

    private final String label;
}
