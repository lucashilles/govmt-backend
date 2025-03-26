package lucashs.dev.lifecycle;

import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lucashs.dev.entities.Usuario;
import lucashs.dev.repositories.UsuarioRepository;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class AdminSeedBean {

    @ConfigProperty(name = "govmt.default.user")
    String name;

    @ConfigProperty(name = "govmt.default.email")
    String email;

    @ConfigProperty(name = "govmt.default.password")
    String password;

    @Inject
    UsuarioRepository usuarioRepository;

    private static final Logger LOG = Logger.getLogger(AdminSeedBean.class);


    @Startup
    @Transactional
    void seedAdmin() {
        LOG.info("Seeding admin user.");
        Usuario usuario = usuarioRepository.find("email", email).firstResult();

        if (usuario != null) {
            LOG.info("User '" + name + "' already exists.");
            return;
        }

        Usuario.add(name, email, password, "Admin");
        LOG.info("Admin user created.");
    }
}
