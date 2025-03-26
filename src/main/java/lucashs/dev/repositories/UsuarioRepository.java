package lucashs.dev.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import lucashs.dev.entities.Usuario;

@ApplicationScoped
public class UsuarioRepository implements PanacheRepositoryBase<Usuario, Integer> {
}
