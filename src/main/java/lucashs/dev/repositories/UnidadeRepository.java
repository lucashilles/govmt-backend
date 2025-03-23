package lucashs.dev.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import lucashs.dev.entities.Unidade;

@ApplicationScoped
public class UnidadeRepository implements PanacheRepositoryBase<Unidade, Integer> {
}
