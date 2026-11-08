package ph.gov.tabunok.drrm.functions.guide.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ph.gov.tabunok.drrm.functions.guide.entity.Guide;
import ph.gov.tabunok.drrm.functions.incident.entity.IncidentType;

/** Used both for reading and saving guides. Steps are one per line. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuideDto {

    private Long id;

    @NotNull(message = "Choose the hazard.")
    private IncidentType hazard;

    private String hazardLabel;

    @NotBlank(message = "Title is required.")
    @Size(max = 120, message = "Title must be 120 characters or less.")
    private String title;

    @Size(max = 500, message = "Summary must be 500 characters or less.")
    private String summary;

    @Size(max = 6000)
    private String beforeSteps;

    @Size(max = 6000)
    private String duringSteps;

    @Size(max = 6000)
    private String afterSteps;

    private int sortOrder;

    private LocalDateTime updatedAt;

    public static GuideDto from(Guide g) {
        return GuideDto.builder()
                .id(g.getId())
                .hazard(g.getHazard())
                .hazardLabel(g.getHazard().getLabel())
                .title(g.getTitle())
                .summary(g.getSummary())
                .beforeSteps(g.getBeforeSteps())
                .duringSteps(g.getDuringSteps())
                .afterSteps(g.getAfterSteps())
                .sortOrder(g.getSortOrder())
                .updatedAt(g.getUpdatedAt())
                .build();
    }
}