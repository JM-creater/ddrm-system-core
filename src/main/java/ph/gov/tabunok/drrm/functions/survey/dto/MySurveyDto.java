package ph.gov.tabunok.drrm.functions.survey.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MySurveyDto {

    private boolean submitted;
    private LocalDateTime submittedAt;
    private Map<String, Object> profile;
    private Map<Long, Integer> answers;
    private List<DomainScore> domains;
    private DomainScore overall;
    /** Statements where the resident's (reverse coded) score is 2 or lower. */
    private List<String> focusAreas;
}