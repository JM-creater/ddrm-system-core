package ph.gov.tabunok.drrm.functions.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @NotBlank(message = "First name is required.")
    @Size(max = 60)
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Size(max = 60)
    private String lastName;

    @NotBlank(message = "Mobile number is required.")
    @Pattern(regexp = "^(09|\\+639)\\d{9}$", message = "Use a valid PH mobile number, e.g. 09171234567.")
    private String contactNumber;

    /** Required for residents; officers may leave it blank. */
    @Size(max = 40)
    private String purok;

    @Size(max = 160, message = "Address is too long.")
    private String address;
}