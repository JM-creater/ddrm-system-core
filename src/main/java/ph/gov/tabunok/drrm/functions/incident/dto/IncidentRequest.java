package ph.gov.tabunok.drrm.functions.incident.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ph.gov.tabunok.drrm.functions.incident.entity.IncidentType;
import ph.gov.tabunok.drrm.functions.incident.entity.Severity;

@Data
public class IncidentRequest {

    @NotNull(message = "Choose the hazard / incident type.")
    private IncidentType type;

    @NotNull(message = "Choose the severity.")
    private Severity severity;

    @NotBlank(message = "Choose the purok.")
    @Size(max = 40)
    private String purok;

    @NotBlank(message = "Describe what happened.")
    @Size(max = 1000, message = "Description must be 1000 characters or less.")
    private String description;

    @Size(max = 200, message = "Exact location must be 200 characters or less.")
    private String exactLocation;

    @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0")
    private Double latitude;

    @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0")
    private Double longitude;
}