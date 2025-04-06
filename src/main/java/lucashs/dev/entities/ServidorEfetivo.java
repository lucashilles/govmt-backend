package lucashs.dev.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "servidor_efetivo")
public class ServidorEfetivo extends PanacheEntityBase {

    @Id
    @Column(name = "pes_id")
    public int id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "pes_id", nullable = false)
    public Pessoa pessoa;

    @Column(name = "se_matricula", nullable = false, length = 20)
    public String matricula;

}
