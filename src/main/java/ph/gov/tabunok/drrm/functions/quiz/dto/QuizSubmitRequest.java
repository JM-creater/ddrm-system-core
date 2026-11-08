package ph.gov.tabunok.drrm.functions.quiz.dto;

import java.util.Map;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class QuizSubmitRequest {

    /** question id -> A, B, C or D */
    @NotEmpty(message = "Answer the questions before submitting.")
    private Map<Long, String> answers;
}