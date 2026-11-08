package ph.gov.tabunok.drrm.functions.survey.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ph.gov.tabunok.drrm.functions.incident.entity.Labeled;

@Getter
@RequiredArgsConstructor
public enum SurveySection implements Labeled {
    KNOWLEDGE("Knowledge on Disaster Preparedness"),
    ATTITUDE("Attitudes Toward Disaster Preparedness"),
    PRACTICE("Disaster Preparedness Practices");

    private final String label;
}
