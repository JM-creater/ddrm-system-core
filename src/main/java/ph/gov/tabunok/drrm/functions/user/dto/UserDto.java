package ph.gov.tabunok.drrm.functions.user.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ph.gov.tabunok.drrm.functions.user.entity.Role;
import ph.gov.tabunok.drrm.functions.user.entity.User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String contactNumber;
    private String purok;
    private String address;
    private Role role;
    private boolean enabled;
    private LocalDateTime createdAt;

    public static UserDto from(User u) {
        return UserDto.builder()
                .id(u.getId())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .fullName(u.getFullName())
                .email(u.getEmail())
                .contactNumber(u.getContactNumber())
                .purok(u.getPurok())
                .address(u.getAddress())
                .role(u.getRole())
                .enabled(u.isEnabled())
                .createdAt(u.getCreatedAt())
                .build();
    }
}