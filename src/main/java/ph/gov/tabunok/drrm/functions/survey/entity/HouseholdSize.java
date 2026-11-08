package ph.gov.tabunok.drrm.functions.survey.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ph.gov.tabunok.drrm.functions.incident.entity.Labeled;

@Getter
@RequiredArgsConstructor
public enum HouseholdSize implements Labeled {
    ONE_TO_TWO("1–2 members"),
    THREE_TO_FOUR("3–4 members"),
    FIVE_TO_SIX("5–6 members"),
    SEVEN_UP("7 or more members");

    private final String label;
}
