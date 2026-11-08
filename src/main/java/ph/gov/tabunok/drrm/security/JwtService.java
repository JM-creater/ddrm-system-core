package ph.gov.tabunok.drrm.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import ph.gov.tabunok.drrm.config.AppProperties;
import ph.gov.tabunok.drrm.functions.user.entity.User;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMillis;

    public JwtService(AppProperties properties) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.getJwt().getSecret()));
        this.expirationMillis = properties.getJwt().getExpirationMinutes() * 60_000L;
    }

    public String generateToken(User user) {
        Date now = new Date();
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("uid", user.getId())
                .claim("role", user.getRole().name())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMillis))
                .signWith(key)
                .compact();
    }

    /** Returns the e-mail stored in the token, or {@code null} if the token is invalid or expired. */
    public String extractEmail(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    public long getExpirationMillis() {
        return expirationMillis;
    }
}