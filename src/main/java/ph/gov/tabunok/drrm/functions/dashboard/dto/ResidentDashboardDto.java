package ph.gov.tabunok.drrm.functions.dashboard.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ph.gov.tabunok.drrm.functions.alert.dto.AlertDto;
import ph.gov.tabunok.drrm.functions.incident.dto.IncidentDto;
import ph.gov.tabunok.drrm.functions.quiz.dto.QuizAttemptDto;
import ph.gov.tabunok.drrm.functions.survey.dto.DomainScore;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResidentDashboardDto {

    private List<AlertDto> activeAlerts;
    private long reportsToday;
    private long pendingReview;
    private long myReportCount;
    private long openEvacuationCenters;
    private QuizAttemptDto latestQuiz;
    private DomainScore readiness;
    private List<DomainScore> readinessDomains;
    private List<CountItem> reportsByType;
    private List<IncidentDto> myRecentReports;
}