package ph.gov.tabunok.drrm.functions.survey.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ph.gov.tabunok.drrm.functions.survey.entity.SurveyResponse;

public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {

    Optional<SurveyResponse> findByUserId(Long userId);

    @Query("select distinct r from SurveyResponse r left join fetch r.answers a left join fetch a.question order by r.id")
    List<SurveyResponse> findAllWithAnswers();

    @Query("select avg(r.overallMean) from SurveyResponse r")
    Double averageOverallMean();
}