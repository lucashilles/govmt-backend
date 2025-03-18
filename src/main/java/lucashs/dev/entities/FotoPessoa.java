package lucashs.dev.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.Date;

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
    public Date data;

    @Column(name = "fp_bucket", nullable = false, length = 50)
    public String bucket;

    @Column(name = "fp_hash", nullable = false, length = 50)
    public String hash;

}
