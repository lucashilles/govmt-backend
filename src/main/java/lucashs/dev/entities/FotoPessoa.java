package lucashs.dev.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.LocalDate;

@Entity
@Table(name = "foto_pessoa")
public class FotoPessoa extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fp_id")
    public int id;

    @ManyToOne
    @JoinColumn(name = "pes_id", nullable = false)
    public Pessoa pessoa;

    @Temporal(TemporalType.DATE)
    @Column(name = "fp_data", nullable = false)
    public LocalDate data;

    @Column(name = "fp_bucket", nullable = false, length = 50)
    public String bucket;

    @Column(name = "fp_hash", nullable = false, length = 50)
    public String hash;

}
