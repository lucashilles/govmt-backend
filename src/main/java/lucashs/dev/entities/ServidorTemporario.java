package lucashs.dev.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.LocalDate;

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
    public LocalDate dataAdmissao;

    @Temporal(TemporalType.DATE)
    @Column(name = "st_data_demissao", nullable = false)
    public LocalDate dataDemissao;

}
