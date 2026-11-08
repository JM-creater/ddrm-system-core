package ph.gov.tabunok.drrm.functions.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ph.gov.tabunok.drrm.functions.auth.dto.AuthResponse;
import ph.gov.tabunok.drrm.functions.auth.dto.LoginRequest;
import ph.gov.tabunok.drrm.functions.auth.dto.RegisterRequest;
import ph.gov.tabunok.drrm.functions.auth.service.AuthService;
import ph.gov.tabunok.drrm.functions.user.dto.UserDto;
import ph.gov.tabunok.drrm.functions.user.service.UserService;
import ph.gov.tabunok.drrm.security.AppUserPrincipal;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest req) {
        return authService.register(req);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @GetMapping("/me")
    public UserDto me(@AuthenticationPrincipal AppUserPrincipal me) {
        return userService.me(me.getId());
    }
}