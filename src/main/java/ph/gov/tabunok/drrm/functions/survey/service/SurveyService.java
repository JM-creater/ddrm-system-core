package ph.gov.tabunok.drrm.functions.survey.service;

import ph.gov.tabunok.drrm.functions.survey.dto.MySurveyDto;
import ph.gov.tabunok.drrm.functions.survey.dto.SurveyAnalyticsDto;
import ph.gov.tabunok.drrm.functions.survey.dto.SurveyFormDto;
import ph.gov.tabunok.drrm.functions.survey.dto.SurveySubmitRequest;

public interface SurveyService {

    SurveyFormDto form();

    MySurveyDto submit(Long userId, SurveySubmitRequest req);

    MySurveyDto mine(Long userId);

    /** Overall readiness and domain scores of one resident, or null when not yet answered. */
    MySurveyDto mineOrNull(Long userId);

    SurveyAnalyticsDto analytics();

    String exportCsv();
}