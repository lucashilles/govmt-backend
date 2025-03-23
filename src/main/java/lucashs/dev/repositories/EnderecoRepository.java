package lucashs.dev.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import lucashs.dev.entities.Endereco;

@ApplicationScoped
public class EnderecoRepository implements PanacheRepositoryBase<Endereco, Integer> {
}