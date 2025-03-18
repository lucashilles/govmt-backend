package lucashs.dev.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "pessoa")
public class Pessoa extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pes_id")
    public int id;

    @Column(name = "pes_nome", nullable = false, length = 200)
    public String nome;

    @Temporal(TemporalType.DATE)
    @Column(name = "pes_data_nascimento", nullable = false)
    public Date dataNascimento;

    @Column(name = "pes_sexo", nullable = false, length = 9)
    public String sexo;

    @Column(name = "pes_mae", nullable = false, length = 200)
    public String mae;

    @Column(name = "pes_pai", nullable = false, length = 200)
    public String pai;

    @ManyToMany
    @JoinTable(
            name = "pessoa_endereco",
            joinColumns = @JoinColumn(name = "pes_id"),
            inverseJoinColumns = @JoinColumn(name = "end_id"))
    public Set<Endereco> enderecos;

    @OneToOne(mappedBy = "pessoa", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrimaryKeyJoinColumn
    private ServidorEfetivo servidorEfetivo;
}
