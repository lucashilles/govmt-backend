package lucashs.dev.security;

import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import org.eclipse.microprofile.jwt.JsonWebToken;

@ApplicationScoped
public class JwtUtils {

    @Inject
    JWTParser jwtParser;

    public String generateToken(String email, String role) {
        Instant now = Instant.now();
        return Jwt.issuer("https://lucashs.dev/")
                .subject(email)
                .expiresAt(now.plus(Duration.ofMinutes(5)))
                .groups(role)
                .sign();
    }

    public String refreshToken(String oldToken) throws ParseException {
        JsonWebToken parsed = jwtParser.parse(oldToken);
        String group = parsed.getGroups().stream().findFirst().orElseThrow();
        return generateToken(parsed.getSubject(), group);
    }

}
