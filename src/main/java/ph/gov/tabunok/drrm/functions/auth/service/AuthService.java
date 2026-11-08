package ph.gov.tabunok.drrm.functions.auth.service;

import ph.gov.tabunok.drrm.functions.auth.dto.AuthResponse;
import ph.gov.tabunok.drrm.functions.auth.dto.LoginRequest;
import ph.gov.tabunok.drrm.functions.auth.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest req);

    AuthResponse login(LoginRequest req);

    void requireValidPurok(String purok);

    static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}