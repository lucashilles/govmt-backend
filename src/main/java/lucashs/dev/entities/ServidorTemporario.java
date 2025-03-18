package lucashs.dev.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "servidor_temporario")
public class ServidorTemporario extends PanacheEntityBase {

    @Id
    @Column(name = "pes_id")
    public int id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "pes_id", nullable = false)
    public Pessoa pessoa;

    @Temporal(TemporalType.DATE)
    @Column(name = "st_data_admissao", nullable = false)
    public Date dataAdmissao;

    @Temporal(TemporalType.DATE)
    @Column(name = "fp_data_demissao", nullable = false)
    public Date dataDemissao;

}
