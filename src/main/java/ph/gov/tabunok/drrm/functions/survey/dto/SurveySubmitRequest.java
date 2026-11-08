package ph.gov.tabunok.drrm.functions.survey.dto;

import java.util.Map;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ph.gov.tabunok.drrm.functions.survey.entity.AgeGroup;
import ph.gov.tabunok.drrm.functions.survey.entity.Education;
import ph.gov.tabunok.drrm.functions.survey.entity.HouseholdSize;
import ph.gov.tabunok.drrm.functions.survey.entity.IncomeBracket;
import ph.gov.tabunok.drrm.functions.survey.entity.ResidencyLength;
import ph.gov.tabunok.drrm.functions.survey.entity.Sex;

@Data
public class SurveySubmitRequest {

    @NotNull(message = "Select your age group.")
    private AgeGroup ageGroup;

    @NotNull(message = "Select your sex.")
    private Sex sex;

    @NotNull(message = "Select your educational attainment.")
    private Education education;

    @NotNull(message = "Select your household size.")
    private HouseholdSize householdSize;

    @NotNull(message = "Select your length of residency.")
    private ResidencyLength residency;

    @NotNull(message = "Select your monthly household income.")
    private IncomeBracket income;

    @NotNull(message = "Tell us if you have experienced a disaster before.")
    private Boolean experiencedDisaster;

    /** question id -> 1..4 */
    @NotEmpty(message = "Answer all statements.")
    private Map<Long, Integer> answers;
}