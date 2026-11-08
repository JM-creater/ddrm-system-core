package ph.gov.tabunok.drrm.functions.survey.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ph.gov.tabunok.drrm.functions.survey.dto.MySurveyDto;
import ph.gov.tabunok.drrm.functions.survey.dto.SurveyFormDto;
import ph.gov.tabunok.drrm.functions.survey.dto.SurveySubmitRequest;
import ph.gov.tabunok.drrm.functions.survey.service.SurveyService;
import ph.gov.tabunok.drrm.security.AppUserPrincipal;

@RestController
@RequestMapping("/api/survey")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    @GetMapping("/form")
    public SurveyFormDto form() {
        return surveyService.form();
    }

    @GetMapping("/me")
    public MySurveyDto mine(@AuthenticationPrincipal AppUserPrincipal me) {
        return surveyService.mine(me.getId());
    }

    /** Only residents are respondents of the study. */
    @PostMapping
    @PreAuthorize("hasRole('RESIDENT')")
    public MySurveyDto submit(@AuthenticationPrincipal AppUserPrincipal me, @Valid @RequestBody SurveySubmitRequest req) {
        return surveyService.submit(me.getId(), req);
    }
}