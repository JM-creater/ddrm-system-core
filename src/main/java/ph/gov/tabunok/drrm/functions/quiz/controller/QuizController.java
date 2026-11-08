package ph.gov.tabunok.drrm.functions.quiz.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ph.gov.tabunok.drrm.functions.quiz.dto.QuizAttemptDto;
import ph.gov.tabunok.drrm.functions.quiz.dto.QuizQuestionDto;
import ph.gov.tabunok.drrm.functions.quiz.dto.QuizResultDto;
import ph.gov.tabunok.drrm.functions.quiz.dto.QuizSubmitRequest;
import ph.gov.tabunok.drrm.functions.quiz.service.QuizService;
import ph.gov.tabunok.drrm.security.AppUserPrincipal;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @GetMapping("/questions")
    public List<QuizQuestionDto> questions() {
        return quizService.questions();
    }

    @PostMapping("/submit")
    public QuizResultDto submit(@AuthenticationPrincipal AppUserPrincipal me, @Valid @RequestBody QuizSubmitRequest req) {
        return quizService.submit(me.getId(), req);
    }

    @GetMapping("/history")
    public List<QuizAttemptDto> history(@AuthenticationPrincipal AppUserPrincipal me) {
        return quizService.history(me.getId());
    }
}