package lucashs.dev.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "lotacao")
public class Lotacao extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lot_id")
    public int id;

    @ManyToOne
    @JoinColumn(name = "pes_id", nullable = false)
    public Pessoa pessoa;

    @ManyToOne
    @JoinColumn(name = "unid_id", nullable = false)
    public Unidade unidade;

    @Temporal(TemporalType.DATE)
    @Column(name = "lot_data_lotacao", nullable = false)
    public Date dataLotacao;

    @Temporal(TemporalType.DATE)
    @Column(name = "lot_data_remocao")
    public Date dataRemocao;

    @Column(name = "lot_portaria", nullable = false, length = 100)
    public String portaria;

}
