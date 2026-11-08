package ph.gov.tabunok.drrm.functions.quiz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ph.gov.tabunok.drrm.functions.quiz.entity.QuizQuestion;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {

    List<QuizQuestion> findAllByOrderBySortOrderAscIdAsc();
}