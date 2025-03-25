package lucashs.dev.repositories;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import lucashs.dev.entities.Lotacao;

@ApplicationScoped
public class LotacaoRepository implements PanacheRepositoryBase<Lotacao, Integer> {
    public PanacheQuery<Lotacao> findByNomeParcialServidorEfetivo(String partialName) {
        return find("FROM Lotacao l " +
                    "JOIN Pessoa p ON l.pessoa.id = p.id " +
                    "JOIN ServidorEfetivo se ON p.id = se.id " +
                    "WHERE LOWER(p.nome) LIKE ?1 ", "%" + partialName.toLowerCase() + "%");
    }
}
