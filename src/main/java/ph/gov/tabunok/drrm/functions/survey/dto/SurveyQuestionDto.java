package ph.gov.tabunok.drrm.functions.survey.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ph.gov.tabunok.drrm.functions.survey.entity.SurveySection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyQuestionDto {

    private Long id;
    private SurveySection section;
    private String sectionLabel;
    private int itemNo;
    private String statement;
    private String indicator;
}