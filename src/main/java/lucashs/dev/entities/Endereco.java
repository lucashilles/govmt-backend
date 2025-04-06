package lucashs.dev.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Set;

@Entity
@Table(name="endereco")
public class Endereco extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "end_id")
    public int id;

    @Column(name = "end_tipo_logradouro", nullable = false, length = 50)
    public String tipoLogradouro;

    @Column(name = "end_logradouro", nullable = false, length = 200)
    public String logradouro;

    @Column(name = "end_numero", nullable = false)
    public int numero;

    @Column(name = "end_bairro", nullable = false, length = 100)
    public String bairro;

    @ManyToOne
    @JoinColumn(name = "cid_id", nullable = false)
    public Cidade cidade;

    @ManyToMany(mappedBy = "enderecos")
    public Set<Unidade> unidades;

    @ManyToMany(mappedBy = "enderecos")
    public Set<Pessoa> pessoas;
}
