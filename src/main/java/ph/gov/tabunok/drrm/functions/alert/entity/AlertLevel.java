package ph.gov.tabunok.drrm.functions.alert.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ph.gov.tabunok.drrm.functions.incident.entity.Labeled;

@Getter
@RequiredArgsConstructor
public enum AlertLevel implements Labeled {
    ADVISORY("Advisory"),
    YELLOW("Yellow warning"),
    ORANGE("Orange warning"),
    RED("Red warning");

    private final String label;
}
