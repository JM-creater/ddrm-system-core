package ph.gov.tabunok.drrm.functions.survey.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ph.gov.tabunok.drrm.functions.incident.entity.Labeled;

@Getter
@RequiredArgsConstructor
public enum AgeGroup implements Labeled {
    AGE_18_25("18–25"),
    AGE_26_35("26–35"),
    AGE_36_45("36–45"),
    AGE_46_55("46–55"),
    AGE_56_UP("56 and above");

    private final String label;
}
