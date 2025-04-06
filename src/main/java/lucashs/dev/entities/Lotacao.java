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
    public LocalDate dataLotacao;

    @Temporal(TemporalType.DATE)
    @Column(name = "lot_data_remocao")
    public LocalDate dataRemocao;

    @Column(name = "lot_portaria", nullable = false, length = 100)
    public String portaria;

}
