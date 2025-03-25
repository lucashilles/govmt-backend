package lucashs.dev.repositories;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import lucashs.dev.entities.Endereco;

@ApplicationScoped
public class EnderecoRepository implements PanacheRepositoryBase<Endereco, Integer> {

    public PanacheQuery<Endereco> findByNomeParcialServidorEfetivo(String partialName) {
        return find("SELECT e.* " +
                    "FROM endereco e " +
                    "JOIN unidade_endereco ue ON e.end_id = ue.end_id " +
                    "JOIN unidade u ON ue.unid_id = u.unid_id " +
                    "JOIN lotacao l ON u.unid_id = l.unid_id " +
                    "JOIN pessoa p ON l.pes_id = p.pes_id " +
                    "JOIN servidor_efetivo se ON p.pes_id = se.pes_id " +
                    "WHERE LOWER(p.pes_nome) LIKE :nomeParcial;", "%" + partialName.toLowerCase() + "%");
    }

}