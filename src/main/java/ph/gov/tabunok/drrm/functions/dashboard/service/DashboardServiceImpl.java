package ph.gov.tabunok.drrm.functions.dashboard.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ph.gov.tabunok.drrm.functions.alert.repository.AlertRepository;
import ph.gov.tabunok.drrm.functions.alert.service.AlertService;
import ph.gov.tabunok.drrm.functions.dashboard.dto.AdminDashboardDto;
import ph.gov.tabunok.drrm.functions.dashboard.dto.CountItem;
import ph.gov.tabunok.drrm.functions.dashboard.dto.ResidentDashboardDto;
import ph.gov.tabunok.drrm.functions.directory.repository.EvacuationCenterRepository;
import ph.gov.tabunok.drrm.functions.incident.entity.IncidentStatus;
import ph.gov.tabunok.drrm.functions.incident.entity.IncidentType;
import ph.gov.tabunok.drrm.functions.incident.repository.IncidentRepository;
import ph.gov.tabunok.drrm.functions.incident.service.IncidentService;
import ph.gov.tabunok.drrm.functions.quiz.service.QuizService;
import ph.gov.tabunok.drrm.functions.survey.dto.MySurveyDto;
import ph.gov.tabunok.drrm.functions.survey.repository.SurveyResponseRepository;
import ph.gov.tabunok.drrm.functions.survey.service.Scale;
import ph.gov.tabunok.drrm.functions.survey.service.SurveyService;
import ph.gov.tabunok.drrm.functions.user.entity.Role;
import ph.gov.tabunok.drrm.functions.user.repository.UserRepository;
import ph.gov.tabunok.drrm.security.AppUserPrincipal;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final IncidentRepository incidentRepository;
    private final AlertRepository alertRepository;
    private final UserRepository userRepository;
    private final SurveyResponseRepository surveyResponseRepository;
    private final EvacuationCenterRepository centerRepository;
    private final AlertService alertService;
    private final SurveyService surveyService;
    private final QuizService quizService;
    private final IncidentService incidentService;

    @Override
    @Transactional(readOnly = true)
    public ResidentDashboardDto resident(AppUserPrincipal me) {
        MySurveyDto survey = surveyService.mineOrNull(me.getId());
        return ResidentDashboardDto.builder()
                .activeAlerts(alertService.active())
                .reportsToday(incidentRepository.countByCreatedAtGreaterThanEqual(LocalDate.now().atStartOfDay()))
                .pendingReview(incidentRepository.countByStatus(IncidentStatus.PENDING))
                .myReportCount(incidentRepository.countByReporterId(me.getId()))
                .openEvacuationCenters(centerRepository.countByOpenTrue())
                .latestQuiz(quizService.latest(me.getId()))
                .readiness(survey == null ? null : survey.getOverall())
                .readinessDomains(survey == null ? List.of() : survey.getDomains())
                .reportsByType(byType())
                .myRecentReports(incidentRepository.findTop5ByReporterIdOrderByCreatedAtDesc(me.getId()).stream()
                        .map(i -> incidentService.toDto(i, me)).toList())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardDto admin(AppUserPrincipal me) {
        Map<IncidentStatus, Long> statusCounts = new EnumMap<>(IncidentStatus.class);
        Arrays.stream(IncidentStatus.values()).forEach(s -> statusCounts.put(s, 0L));
        for (Object[] row : incidentRepository.countGroupedByStatus()) {
            statusCounts.put((IncidentStatus) row[0], (Long) row[1]);
        }
        long total = statusCounts.values().stream().mapToLong(Long::longValue).sum();
        Double avg = surveyResponseRepository.averageOverallMean();

        return AdminDashboardDto.builder()
                .residents(userRepository.countByRole(Role.RESIDENT))
                .totalIncidents(total)
                .reportsToday(incidentRepository.countByCreatedAtGreaterThanEqual(LocalDate.now().atStartOfDay()))
                .pending(statusCounts.get(IncidentStatus.PENDING))
                .activeAlerts(alertRepository.countByActiveTrue())
                .respondents(surveyResponseRepository.count())
                .averageReadiness(avg == null ? null : Scale.score("OVERALL", "Average readiness", avg))
                .byType(byType())
                .byStatus(statusCounts.entrySet().stream()
                        .map(e -> new CountItem(e.getKey().name(), e.getKey().getLabel(), e.getValue())).toList())
                .byPurok(incidentRepository.countByPurok(IncidentStatus.REJECTED).stream()
                        .map(r -> new CountItem((String) r[0], (String) r[0], (Long) r[1])).toList())
                .pendingQueue(incidentRepository.findTop6ByStatusOrderByCreatedAtAsc(IncidentStatus.PENDING).stream()
                        .map(i -> incidentService.toDto(i, me)).toList())
                .build();
    }

    private List<CountItem> byType() {
        Map<IncidentType, Long> counts = new EnumMap<>(IncidentType.class);
        Arrays.stream(IncidentType.values()).forEach(t -> counts.put(t, 0L));
        for (Object[] row : incidentRepository.countByType(IncidentStatus.REJECTED)) {
            counts.put((IncidentType) row[0], (Long) row[1]);
        }
        return counts.entrySet().stream()
                .map(e -> new CountItem(e.getKey().name(), e.getKey().getLabel(), e.getValue()))
                .toList();
    }
}