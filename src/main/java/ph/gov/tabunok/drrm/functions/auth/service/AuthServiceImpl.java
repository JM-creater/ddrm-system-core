package ph.gov.tabunok.drrm.functions.auth.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ph.gov.tabunok.drrm.config.AppProperties;
import ph.gov.tabunok.drrm.exception.ApiException;
import ph.gov.tabunok.drrm.functions.auth.dto.AuthResponse;
import ph.gov.tabunok.drrm.functions.auth.dto.LoginRequest;
import ph.gov.tabunok.drrm.functions.auth.dto.RegisterRequest;
import ph.gov.tabunok.drrm.functions.user.dto.UserDto;
import ph.gov.tabunok.drrm.functions.user.entity.Role;
import ph.gov.tabunok.drrm.functions.user.entity.User;
import ph.gov.tabunok.drrm.functions.user.repository.UserRepository;
import ph.gov.tabunok.drrm.security.JwtService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AppProperties properties;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest req) {
        String email = req.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw ApiException.conflict("An account with this email address already exists.");
        }
        requireValidPurok(req.getPurok());
        User user = User.builder()
                .firstName(req.getFirstName().trim())
                .lastName(req.getLastName().trim())
                .email(email)
                .contactNumber(req.getContactNumber().trim())
                .purok(req.getPurok())
                .address(AuthService.blankToNull(req.getAddress()))
                .password(passwordEncoder.encode(req.getPassword()))
                .role(Role.RESIDENT)
                .enabled(true)
                .build();
        userRepository.save(user);
        return issue(user);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmailIgnoreCase(req.getEmail().trim())
                .filter(u -> passwordEncoder.matches(req.getPassword(), u.getPassword()))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Incorrect email or password."));
        if (!user.isEnabled()) {
            throw new ApiException(HttpStatus.FORBIDDEN,
                    "This account has been deactivated. Please contact the barangay DRRM office.");
        }
        return issue(user);
    }

    @Override
    public void requireValidPurok(String purok) {
        if (!properties.getPuroks().isEmpty() && !properties.getPuroks().contains(purok)) {
            throw ApiException.badRequest("Please choose a valid purok.");
        }
    }

    private AuthResponse issue(User user) {
        return AuthResponse.builder()
                .token(jwtService.generateToken(user))
                .expiresIn(jwtService.getExpirationMillis())
                .user(UserDto.from(user))
                .build();
    }
}