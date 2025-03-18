package lucashs.dev.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name="unidade")
public class Unidade extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unid_id")
    public int id;

    @Column(name = "unid_nome", nullable = false, length = 200)
    public String nome;

    @Column(name = "unid_sigla", nullable = false, length = 20)
    public String sigla;

    @ManyToMany
    @JoinTable(
            name = "unidade_endereco",
            joinColumns = @JoinColumn(name = "unid_id"),
            inverseJoinColumns = @JoinColumn(name = "end_id"))
    public Set<Endereco> enderecos;

}
