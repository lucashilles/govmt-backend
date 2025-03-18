package lucashs.dev.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name="cidade")
public class Cidade extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cid_id")
    public int id;

    @Column(name = "cid_nome", nullable = false, length = 200)
    public String nome;

    @Column(name = "cid_uf", nullable = false, length = 2)
    public String uf;

}
