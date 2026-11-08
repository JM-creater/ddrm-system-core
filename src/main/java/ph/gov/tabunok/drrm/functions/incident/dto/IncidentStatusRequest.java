package ph.gov.tabunok.drrm.functions.incident.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ph.gov.tabunok.drrm.functions.incident.entity.IncidentStatus;

@Data
public class IncidentStatusRequest {

    @NotNull(message = "Choose a status.")
    private IncidentStatus status;

    @Size(max = 500, message = "Remarks must be 500 characters or less.")
    private String remarks;
}