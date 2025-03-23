package lucashs.dev.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import lucashs.dev.entities.ServidorEfetivo;

@ApplicationScoped
public class ServidorEfetivoRepository implements PanacheRepositoryBase<ServidorEfetivo, Integer> {
}
