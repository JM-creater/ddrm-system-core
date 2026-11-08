package ph.gov.tabunok.drrm.functions.user.service;

import ph.gov.tabunok.drrm.functions.incident.dto.PageResponse;
import ph.gov.tabunok.drrm.functions.user.dto.ChangePasswordRequest;
import ph.gov.tabunok.drrm.functions.user.dto.UpdateProfileRequest;
import ph.gov.tabunok.drrm.functions.user.dto.UserDto;

public interface UserService {

    UserDto me(Long userId);

    UserDto updateProfile(Long userId, UpdateProfileRequest req);

    void changePassword(Long userId, ChangePasswordRequest req);

    PageResponse<UserDto> listResidents(String q, int page, int size);

    UserDto setEnabled(Long targetId, boolean enabled, Long actingAdminId);
}