package ph.gov.tabunok.drrm.functions.quiz.service;

import java.util.List;

import ph.gov.tabunok.drrm.functions.quiz.dto.QuizAttemptDto;
import ph.gov.tabunok.drrm.functions.quiz.dto.QuizQuestionDto;
import ph.gov.tabunok.drrm.functions.quiz.dto.QuizResultDto;
import ph.gov.tabunok.drrm.functions.quiz.dto.QuizSubmitRequest;

public interface QuizService {

    List<QuizQuestionDto> questions();

    QuizResultDto submit(Long userId, QuizSubmitRequest req);

    List<QuizAttemptDto> history(Long userId);

    QuizAttemptDto latest(Long userId);

    static String rating(int percent) {
        if (percent >= 90) return "Excellent";
        if (percent >= 75) return "Good";
        if (percent >= 50) return "Fair";
        return "Needs improvement";
    }
}