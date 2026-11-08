package ph.gov.tabunok.drrm.functions.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "Enter your current password.")
    private String currentPassword;

    @NotBlank(message = "Enter a new password.")
    @Size(min = 8, max = 72, message = "Password must be 8 to 72 characters.")
    private String newPassword;
}