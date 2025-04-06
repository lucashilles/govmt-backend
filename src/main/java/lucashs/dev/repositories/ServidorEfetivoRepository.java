package lucashs.dev.repositories;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import lucashs.dev.entities.ServidorEfetivo;

@ApplicationScoped
public class ServidorEfetivoRepository implements PanacheRepositoryBase<ServidorEfetivo, Integer> {
    public PanacheQuery<ServidorEfetivo> getByUnidadeId(int unidadeId) {
        return find("FROM ServidorEfetivo se " +
                    "JOIN Pessoa p ON p.id = se.pessoa.id " +
                    "JOIN Lotacao l ON p.id = l.pessoa.id " +
                    "WHERE l.unidade.id = ?1", unidadeId);
    }
}
