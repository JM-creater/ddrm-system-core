package ph.gov.tabunok.drrm.functions.quiz.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestionDto {

    private Long id;
    private String question;
    private List<OptionDto> options;
}