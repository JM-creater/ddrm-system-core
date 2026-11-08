package ph.gov.tabunok.drrm.functions.alert.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ph.gov.tabunok.drrm.functions.alert.entity.Alert;
import ph.gov.tabunok.drrm.functions.alert.entity.AlertLevel;
import ph.gov.tabunok.drrm.functions.incident.entity.IncidentType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertDto {

    private Long id;
    private String title;
    private String message;
    private AlertLevel level;
    private String levelLabel;
    private IncidentType hazard;
    private String hazardLabel;
    private String affectedArea;
    private boolean active;
    private String postedBy;
    private LocalDateTime createdAt;
    private LocalDateTime liftedAt;

    public static AlertDto from(Alert a) {
        return AlertDto.builder()
                .id(a.getId())
                .title(a.getTitle())
                .message(a.getMessage())
                .level(a.getLevel())
                .levelLabel(a.getLevel().getLabel())
                .hazard(a.getHazard())
                .hazardLabel(a.getHazard() == null ? null : a.getHazard().getLabel())
                .affectedArea(a.getAffectedArea())
                .active(a.isActive())
                .postedBy(a.getPostedBy() == null ? null : a.getPostedBy().getFullName())
                .createdAt(a.getCreatedAt())
                .liftedAt(a.getLiftedAt())
                .build();
    }
}