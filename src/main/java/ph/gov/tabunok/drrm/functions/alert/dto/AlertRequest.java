package ph.gov.tabunok.drrm.functions.alert.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ph.gov.tabunok.drrm.functions.alert.entity.AlertLevel;
import ph.gov.tabunok.drrm.functions.incident.entity.IncidentType;

@Data
public class AlertRequest {

    @NotBlank(message = "Title is required.")
    @Size(max = 150, message = "Title must be 150 characters or less.")
    private String title;

    @NotBlank(message = "Message is required.")
    @Size(max = 2000, message = "Message must be 2000 characters or less.")
    private String message;

    @NotNull(message = "Choose the warning level.")
    private AlertLevel level;

    private IncidentType hazard;

    @Size(max = 150, message = "Affected area must be 150 characters or less.")
    private String affectedArea;
}