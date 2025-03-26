package lucashs.dev.resources;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.security.UnauthorizedException;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.Instant;
import java.util.Map;
import lucashs.dev.DTOs.UsuarioDTO;
import lucashs.dev.entities.Usuario;
import lucashs.dev.repositories.UsuarioRepository;
import lucashs.dev.security.JwtUtils;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    JsonWebToken jwt;

    @Inject
    JwtUtils jwtUtils;

    @Inject
    UsuarioRepository usuarioRepository;

    @POST
    @Path("/login")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response login(
            @FormParam("email") String email,
            @FormParam("password") String password
    ) {
        Usuario usuario = usuarioRepository.find("email", email).firstResult();

        if (usuario == null || !BcryptUtil.matches(password, usuario.password)) {
            throw new BadRequestException();
        }

        String token = jwtUtils.generateToken(usuario.email, usuario.role);

        return Response.ok(Map.of("token", token)).build();
    }

    @POST
    @Path("/refresh")
    public Response refreshToken(@HeaderParam("Authorization") String auth) {
        if (auth == null || auth.isBlank() || !auth.startsWith("Bearer ")) {
            throw new UnauthorizedException();
        }

        Instant exp = Instant.ofEpochSecond(jwt.getExpirationTime());
        if (exp.isBefore(Instant.now())) {
            throw new UnauthorizedException("Token expired.");
        }

        String oldToken = auth.substring(7);
        String token = "";
        try {
            token = jwtUtils.refreshToken(oldToken);
            if (token.isBlank()) {
                throw new InternalServerErrorException();
            }
        } catch (ParseException e) {
            throw new UnauthorizedException("Invalid token");
        }

        return Response.ok(Map.of("token", token)).build();
    }

    @POST
    @Path("/register")
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public Response register(UsuarioDTO dto) {
        Usuario usuario = usuarioRepository.find("email", dto.email).firstResult();
        if (usuario != null) {
            throw new BadRequestException();
        }

        Usuario.add(dto.nome, dto.email, dto.password, "User");
        return Response.ok().build();
    }
}
