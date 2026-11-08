package ph.gov.tabunok.drrm.functions.survey.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ph.gov.tabunok.drrm.functions.quiz.dto.OptionDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyFormDto {

    private List<SurveyQuestionDto> questions;
    /** Part I choices keyed by field name (ageGroup, sex, education ...). */
    private Map<String, List<OptionDto>> profileOptions;
    private List<OptionDto> scale;
}