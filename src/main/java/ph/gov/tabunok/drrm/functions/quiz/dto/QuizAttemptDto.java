package ph.gov.tabunok.drrm.functions.quiz.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ph.gov.tabunok.drrm.functions.quiz.entity.QuizAttempt;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptDto {

    private Long id;
    private int score;
    private int total;
    private int percent;
    private LocalDateTime takenAt;

    public static QuizAttemptDto from(QuizAttempt a) {
        return new QuizAttemptDto(a.getId(), a.getScore(), a.getTotal(), a.getPercent(), a.getTakenAt());
    }
}