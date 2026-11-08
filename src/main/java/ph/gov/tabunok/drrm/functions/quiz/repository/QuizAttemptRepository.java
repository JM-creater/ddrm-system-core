package ph.gov.tabunok.drrm.functions.quiz.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ph.gov.tabunok.drrm.functions.quiz.entity.QuizAttempt;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    Optional<QuizAttempt> findFirstByUserIdOrderByTakenAtDescIdDesc(Long userId);

    List<QuizAttempt> findTop5ByUserIdOrderByTakenAtDescIdDesc(Long userId);
}