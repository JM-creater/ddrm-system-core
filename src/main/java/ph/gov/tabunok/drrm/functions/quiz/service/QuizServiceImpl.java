package ph.gov.tabunok.drrm.functions.quiz.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ph.gov.tabunok.drrm.exception.ApiException;
import ph.gov.tabunok.drrm.functions.quiz.dto.OptionDto;
import ph.gov.tabunok.drrm.functions.quiz.dto.QuizAttemptDto;
import ph.gov.tabunok.drrm.functions.quiz.dto.QuizQuestionDto;
import ph.gov.tabunok.drrm.functions.quiz.dto.QuizResultDto;
import ph.gov.tabunok.drrm.functions.quiz.dto.QuizSubmitRequest;
import ph.gov.tabunok.drrm.functions.quiz.entity.QuizAttempt;
import ph.gov.tabunok.drrm.functions.quiz.entity.QuizQuestion;
import ph.gov.tabunok.drrm.functions.quiz.repository.QuizAttemptRepository;
import ph.gov.tabunok.drrm.functions.quiz.repository.QuizQuestionRepository;
import ph.gov.tabunok.drrm.functions.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizQuestionRepository questionRepository;
    private final QuizAttemptRepository attemptRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<QuizQuestionDto> questions() {
        return questionRepository.findAllByOrderBySortOrderAscIdAsc().stream()
                .map(q -> new QuizQuestionDto(q.getId(), q.getQuestion(), List.of(
                        new OptionDto("A", q.getOptionA()),
                        new OptionDto("B", q.getOptionB()),
                        new OptionDto("C", q.getOptionC()),
                        new OptionDto("D", q.getOptionD()))))
                .toList();
    }

    @Override
    @Transactional
    public QuizResultDto submit(Long userId, QuizSubmitRequest req) {
        List<QuizQuestion> questions = questionRepository.findAllByOrderBySortOrderAscIdAsc();
        if (questions.isEmpty()) throw ApiException.badRequest("There are no test questions yet.");
        Map<Long, String> answers = req.getAnswers();

        int score = 0;
        List<QuizResultDto.Item> items = new ArrayList<>();
        for (QuizQuestion q : questions) {
            String selected = answers.get(q.getId());
            if (selected == null || !selected.matches("[ABCD]")) {
                throw ApiException.badRequest("Please answer all " + questions.size() + " questions.");
            }
            boolean correct = selected.equals(q.getCorrectOption());
            if (correct) score++;
            items.add(QuizResultDto.Item.builder()
                    .questionId(q.getId())
                    .question(q.getQuestion())
                    .selected(selected)
                    .correct(q.getCorrectOption())
                    .correctText(optionText(q, q.getCorrectOption()))
                    .correctAnswer(correct)
                    .explanation(q.getExplanation())
                    .build());
        }

        QuizAttempt attempt = attemptRepository.saveAndFlush(QuizAttempt.builder()
                .user(userRepository.getReferenceById(userId))
                .score(score)
                .total(questions.size())
                .build());

        return QuizResultDto.builder()
                .attemptId(attempt.getId())
                .score(score)
                .total(questions.size())
                .percent(attempt.getPercent())
                .rating(QuizService.rating(attempt.getPercent()))
                .takenAt(attempt.getTakenAt())
                .items(items)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizAttemptDto> history(Long userId) {
        return attemptRepository.findTop5ByUserIdOrderByTakenAtDescIdDesc(userId).stream()
                .map(QuizAttemptDto::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public QuizAttemptDto latest(Long userId) {
        return attemptRepository.findFirstByUserIdOrderByTakenAtDescIdDesc(userId).map(QuizAttemptDto::from)
                .orElse(null);
    }

    private static String optionText(QuizQuestion q, String key) {
        return switch (key) {
            case "A" -> q.getOptionA();
            case "B" -> q.getOptionB();
            case "C" -> q.getOptionC();
            default -> q.getOptionD();
        };
    }
}