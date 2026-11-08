package ph.gov.tabunok.drrm.functions.survey.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainScore {

    private String key;
    private String label;
    private double mean;
    /** (mean - 1) / 3 expressed as 0-100 for gauges. */
    private int percent;
    private String level;
}