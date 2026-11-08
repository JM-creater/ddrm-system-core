package ph.gov.tabunok.drrm.functions.survey.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ph.gov.tabunok.drrm.functions.incident.entity.Labeled;

@Getter
@RequiredArgsConstructor
public enum ResidencyLength implements Labeled {
    UNDER_1("Less than 1 year"),
    ONE_TO_5("1–5 years"),
    SIX_TO_10("6–10 years"),
    OVER_10("More than 10 years");

    private final String label;
}
