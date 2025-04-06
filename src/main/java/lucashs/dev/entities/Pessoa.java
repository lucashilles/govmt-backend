package lucashs.dev.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.LocalDate;
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
    public LocalDate dataNascimento;

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

    @OneToOne(mappedBy = "pessoa", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrimaryKeyJoinColumn
    private ServidorTemporario servidorTemporario;

    public ServidorEfetivo getServidorEfetivo() {
        return servidorEfetivo;
    }

    public ServidorTemporario getServidorTemporario() {
        return servidorTemporario;
    }
}
