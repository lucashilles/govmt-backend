package lucashs.dev.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import lucashs.dev.entities.ServidorTemporario;

@ApplicationScoped
public class ServidorTemporarioRepository implements PanacheRepositoryBase<ServidorTemporario, Integer> {
}
