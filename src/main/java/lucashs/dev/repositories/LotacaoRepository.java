package lucashs.dev.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import lucashs.dev.entities.Lotacao;

@ApplicationScoped
public class LotacaoRepository implements PanacheRepositoryBase<Lotacao, Integer> {
}
