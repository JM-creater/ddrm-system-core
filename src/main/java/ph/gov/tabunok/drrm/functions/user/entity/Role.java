package ph.gov.tabunok.drrm.functions.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ph.gov.tabunok.drrm.functions.incident.entity.Labeled;

@Getter
@RequiredArgsConstructor
public enum Role implements Labeled {
    RESIDENT("Resident"),
    ADMIN("Barangay DRRM Officer");

    private final String label;
}
