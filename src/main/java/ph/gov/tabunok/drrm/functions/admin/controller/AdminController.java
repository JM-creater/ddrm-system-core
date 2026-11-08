package ph.gov.tabunok.drrm.functions.admin.controller;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ph.gov.tabunok.drrm.functions.alert.dto.AlertDto;
import ph.gov.tabunok.drrm.functions.alert.dto.AlertRequest;
import ph.gov.tabunok.drrm.functions.alert.service.AlertService;
import ph.gov.tabunok.drrm.functions.directory.dto.EvacuationCenterDto;
import ph.gov.tabunok.drrm.functions.directory.dto.HotlineDto;
import ph.gov.tabunok.drrm.functions.directory.service.DirectoryService;
import ph.gov.tabunok.drrm.functions.guide.dto.GuideDto;
import ph.gov.tabunok.drrm.functions.guide.service.GuideService;
import ph.gov.tabunok.drrm.functions.incident.controller.CsvResponses;
import ph.gov.tabunok.drrm.functions.incident.dto.IncidentDto;
import ph.gov.tabunok.drrm.functions.incident.dto.IncidentStatusRequest;
import ph.gov.tabunok.drrm.functions.incident.dto.PageResponse;
import ph.gov.tabunok.drrm.functions.incident.service.IncidentService;
import ph.gov.tabunok.drrm.functions.survey.dto.SurveyAnalyticsDto;
import ph.gov.tabunok.drrm.functions.survey.service.SurveyService;
import ph.gov.tabunok.drrm.functions.user.dto.UserDto;
import ph.gov.tabunok.drrm.functions.user.dto.UserStatusRequest;
import ph.gov.tabunok.drrm.functions.user.service.UserService;
import ph.gov.tabunok.drrm.security.AppUserPrincipal;

/** Barangay DRRM officer endpoints. Access is restricted to ROLE_ADMIN in SecurityConfig. */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final IncidentService incidentService;
    private final AlertService alertService;
    private final GuideService guideService;
    private final DirectoryService directoryService;
    private final SurveyService surveyService;
    private final UserService userService;

    // ---- incident reports ----

    @PatchMapping("/incidents/{id}/status")
    public IncidentDto updateIncidentStatus(@AuthenticationPrincipal AppUserPrincipal me, @PathVariable Long id,
                                            @Valid @RequestBody IncidentStatusRequest req) {
        return incidentService.updateStatus(me, id, req);
    }

    // ---- alerts ----

    @PostMapping("/alerts")
    @ResponseStatus(HttpStatus.CREATED)
    public AlertDto createAlert(@AuthenticationPrincipal AppUserPrincipal me, @Valid @RequestBody AlertRequest req) {
        return alertService.create(me.getId(), req);
    }

    @PutMapping("/alerts/{id}")
    public AlertDto updateAlert(@PathVariable Long id, @Valid @RequestBody AlertRequest req) {
        return alertService.update(id, req);
    }

    @PatchMapping("/alerts/{id}/active")
    public AlertDto setAlertActive(@PathVariable Long id, @RequestParam boolean active) {
        return alertService.setActive(id, active);
    }

    @DeleteMapping("/alerts/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAlert(@PathVariable Long id) {
        alertService.delete(id);
    }

    // ---- guides ----

    @PostMapping("/guides")
    @ResponseStatus(HttpStatus.CREATED)
    public GuideDto createGuide(@Valid @RequestBody GuideDto req) {
        return guideService.create(req);
    }

    @PutMapping("/guides/{id}")
    public GuideDto updateGuide(@PathVariable Long id, @Valid @RequestBody GuideDto req) {
        return guideService.update(id, req);
    }

    @DeleteMapping("/guides/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGuide(@PathVariable Long id) {
        guideService.delete(id);
    }

    // ---- directory ----

    @PostMapping("/directory/centers")
    @ResponseStatus(HttpStatus.CREATED)
    public EvacuationCenterDto createCenter(@Valid @RequestBody EvacuationCenterDto req) {
        return directoryService.saveCenter(null, req);
    }

    @PutMapping("/directory/centers/{id}")
    public EvacuationCenterDto updateCenter(@PathVariable Long id, @Valid @RequestBody EvacuationCenterDto req) {
        return directoryService.saveCenter(id, req);
    }

    @DeleteMapping("/directory/centers/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCenter(@PathVariable Long id) {
        directoryService.deleteCenter(id);
    }

    @PostMapping("/directory/hotlines")
    @ResponseStatus(HttpStatus.CREATED)
    public HotlineDto createHotline(@Valid @RequestBody HotlineDto req) {
        return directoryService.saveHotline(null, req);
    }

    @PutMapping("/directory/hotlines/{id}")
    public HotlineDto updateHotline(@PathVariable Long id, @Valid @RequestBody HotlineDto req) {
        return directoryService.saveHotline(id, req);
    }

    @DeleteMapping("/directory/hotlines/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHotline(@PathVariable Long id) {
        directoryService.deleteHotline(id);
    }

    // ---- KAP assessment results ----

    @GetMapping("/survey/analytics")
    public SurveyAnalyticsDto surveyAnalytics() {
        return surveyService.analytics();
    }

    @GetMapping("/survey/export")
    public ResponseEntity<byte[]> exportSurvey() {
        return CsvResponses.attachment(surveyService.exportCsv(), "tabunok-kap-responses-" + LocalDate.now() + ".csv");
    }

    // ---- residents ----

    @GetMapping("/users")
    public PageResponse<UserDto> users(@RequestParam(required = false) String q,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "15") int size) {
        return userService.listResidents(q, page, size);
    }

    @PatchMapping("/users/{id}/status")
    public UserDto setUserStatus(@AuthenticationPrincipal AppUserPrincipal me, @PathVariable Long id,
                                 @Valid @RequestBody UserStatusRequest req) {
        return userService.setEnabled(id, req.getEnabled(), me.getId());
    }
}