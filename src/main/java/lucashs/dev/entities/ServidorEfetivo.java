package lucashs.dev.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.Date;

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
