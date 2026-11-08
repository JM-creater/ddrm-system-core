package ph.gov.tabunok.drrm.functions.user.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ph.gov.tabunok.drrm.exception.ApiException;
import ph.gov.tabunok.drrm.functions.auth.service.AuthService;
import ph.gov.tabunok.drrm.functions.incident.dto.PageResponse;
import ph.gov.tabunok.drrm.functions.user.dto.ChangePasswordRequest;
import ph.gov.tabunok.drrm.functions.user.dto.UpdateProfileRequest;
import ph.gov.tabunok.drrm.functions.user.dto.UserDto;
import ph.gov.tabunok.drrm.functions.user.entity.Role;
import ph.gov.tabunok.drrm.functions.user.entity.User;
import ph.gov.tabunok.drrm.functions.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @Override
    @Transactional(readOnly = true)
    public UserDto me(Long userId) {
        return UserDto.from(find(userId));
    }

    @Override
    @Transactional
    public UserDto updateProfile(Long userId, UpdateProfileRequest req) {
        User user = find(userId);
        String purok = AuthService.blankToNull(req.getPurok());
        if (purok == null && user.getRole() == Role.RESIDENT) {
            throw ApiException.badRequest("Please choose your purok.");
        }
        if (purok != null) authService.requireValidPurok(purok);
        user.setFirstName(req.getFirstName().trim());
        user.setLastName(req.getLastName().trim());
        user.setContactNumber(req.getContactNumber().trim());
        user.setPurok(purok);
        user.setAddress(AuthService.blankToNull(req.getAddress()));
        return UserDto.from(user);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest req) {
        User user = find(userId);
        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
            throw ApiException.badRequest("Your current password is incorrect.");
        }
        if (passwordEncoder.matches(req.getNewPassword(), user.getPassword())) {
            throw ApiException.badRequest("The new password must be different from the current one.");
        }
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserDto> listResidents(String q, int page, int size) {
        String like = q == null || q.isBlank() ? null : "%" + q.trim().toLowerCase() + "%";
        var pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        return PageResponse.of(userRepository.search(Role.RESIDENT, like, pageable), UserDto::from);
    }

    @Override
    @Transactional
    public UserDto setEnabled(Long targetId, boolean enabled, Long actingAdminId) {
        if (targetId.equals(actingAdminId)) {
            throw ApiException.badRequest("You cannot change the status of your own account.");
        }
        User user = find(targetId);
        if (user.getRole() == Role.ADMIN) {
            throw ApiException.forbidden("Officer accounts cannot be deactivated here.");
        }
        user.setEnabled(enabled);
        return UserDto.from(user);
    }

    private User find(Long id) {
        return userRepository.findById(id).orElseThrow(() -> ApiException.notFound("User"));
    }
}