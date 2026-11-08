package ph.gov.tabunok.drrm.functions.dashboard.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ph.gov.tabunok.drrm.functions.incident.dto.IncidentDto;
import ph.gov.tabunok.drrm.functions.survey.dto.DomainScore;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDto {

    private long residents;
    private long totalIncidents;
    private long reportsToday;
    private long pending;
    private long activeAlerts;
    private long respondents;
    private DomainScore averageReadiness;
    private List<CountItem> byType;
    private List<CountItem> byStatus;
    private List<CountItem> byPurok;
    private List<IncidentDto> pendingQueue;
}