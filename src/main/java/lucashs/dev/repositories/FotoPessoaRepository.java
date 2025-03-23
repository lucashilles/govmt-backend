package lucashs.dev.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import lucashs.dev.entities.FotoPessoa;

@ApplicationScoped
public class FotoPessoaRepository implements PanacheRepositoryBase<FotoPessoa, Integer> {
}
