package lucashs.dev.repositories;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import lucashs.dev.common.PagedList;
import lucashs.dev.entities.Cidade;

@ApplicationScoped
public class CidadeRepository implements PanacheRepositoryBase<Cidade, Integer> {

    public PagedList<Cidade> findByNome(String nome, Page page) {
        PanacheQuery<Cidade> pagedQuery = Cidade.find("LOWER(nome) LIKE ?1",
                "%" + nome.toLowerCase() + "%").page(page);
        return new PagedList<>(pagedQuery.list(), page.index + 1, pagedQuery.pageCount(), page.size,
                pagedQuery.count());
    }
}
