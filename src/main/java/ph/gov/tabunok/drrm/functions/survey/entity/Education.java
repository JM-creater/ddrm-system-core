package ph.gov.tabunok.drrm.functions.survey.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ph.gov.tabunok.drrm.functions.incident.entity.Labeled;

@Getter
@RequiredArgsConstructor
public enum Education implements Labeled {
    JUNIOR_HIGH_OR_BELOW("Junior High or below"),
    SENIOR_HIGH("Senior High"),
    COLLEGE("College"),
    POSTGRADUATE("Postgraduate");

    private final String label;
}
