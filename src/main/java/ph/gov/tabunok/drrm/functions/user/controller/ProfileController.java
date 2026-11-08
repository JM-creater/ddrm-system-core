package ph.gov.tabunok.drrm.functions.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ph.gov.tabunok.drrm.functions.user.dto.ChangePasswordRequest;
import ph.gov.tabunok.drrm.functions.user.dto.UpdateProfileRequest;
import ph.gov.tabunok.drrm.functions.user.dto.UserDto;
import ph.gov.tabunok.drrm.functions.user.service.UserService;
import ph.gov.tabunok.drrm.security.AppUserPrincipal;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping
    public UserDto get(@AuthenticationPrincipal AppUserPrincipal me) {
        return userService.me(me.getId());
    }

    @PutMapping
    public UserDto update(@AuthenticationPrincipal AppUserPrincipal me, @Valid @RequestBody UpdateProfileRequest req) {
        return userService.updateProfile(me.getId(), req);
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@AuthenticationPrincipal AppUserPrincipal me,
                               @Valid @RequestBody ChangePasswordRequest req) {
        userService.changePassword(me.getId(), req);
    }
}