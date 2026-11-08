package ph.gov.tabunok.drrm.functions.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "First name is required.")
    @Size(max = 60, message = "First name is too long.")
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Size(max = 60, message = "Last name is too long.")
    private String lastName;

    @NotBlank(message = "Email address is required.")
    @Email(message = "Enter a valid email address.")
    @Size(max = 120, message = "Email address is too long.")
    private String email;

    @NotBlank(message = "Mobile number is required.")
    @Pattern(regexp = "^(09|\\+639)\\d{9}$", message = "Use a valid PH mobile number, e.g. 09171234567.")
    private String contactNumber;

    @NotBlank(message = "Please choose your purok.")
    @Size(max = 40)
    private String purok;

    @Size(max = 160, message = "Address is too long.")
    private String address;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, max = 72, message = "Password must be 8 to 72 characters.")
    private String password;
}