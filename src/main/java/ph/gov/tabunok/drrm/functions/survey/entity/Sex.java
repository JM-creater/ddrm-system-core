package ph.gov.tabunok.drrm.functions.survey.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ph.gov.tabunok.drrm.functions.incident.entity.Labeled;

@Getter
@RequiredArgsConstructor
public enum Sex implements Labeled {
    MALE("Male"),
    FEMALE("Female");

    private final String label;
}
