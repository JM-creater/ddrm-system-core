package ph.gov.tabunok.drrm.functions.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserStatusRequest {

    @NotNull
    private Boolean enabled;
}