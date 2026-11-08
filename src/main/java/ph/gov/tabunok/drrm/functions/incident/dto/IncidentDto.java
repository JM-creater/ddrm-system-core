package ph.gov.tabunok.drrm.functions.incident.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ph.gov.tabunok.drrm.functions.incident.entity.IncidentStatus;
import ph.gov.tabunok.drrm.functions.incident.entity.IncidentType;
import ph.gov.tabunok.drrm.functions.incident.entity.Severity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentDto {

    private Long id;
    private String reportNo;
    private IncidentType type;
    private String typeLabel;
    private Severity severity;
    private String severityLabel;
    private String purok;
    private String description;
    private String exactLocation;
    private Double latitude;
    private Double longitude;
    private IncidentStatus status;
    private String statusLabel;
    private String remarks;
    private Long reporterId;
    private String reporterName;
    private String reporterContact;
    private String reviewedByName;
    private List<MediaDto> media;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean canDelete;
}