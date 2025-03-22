package lucashs.dev.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import lucashs.dev.entities.Pessoa;

@ApplicationScoped
public class PessoaRepository implements PanacheRepositoryBase<Pessoa, Integer> {
}
