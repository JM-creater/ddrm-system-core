package ph.gov.tabunok.drrm.functions.quiz.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultDto {

    private Long attemptId;
    private int score;
    private int total;
    private int percent;
    private String rating;
    private LocalDateTime takenAt;
    private List<Item> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private Long questionId;
        private String question;
        private String selected;
        private String correct;
        private String correctText;
        private boolean correctAnswer;
        private String explanation;
    }
}