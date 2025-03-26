package lucashs.dev.entities;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="usuario")
@UserDefinition
public class Usuario extends PanacheEntity {
    public String nome;
    @Username
    public String email;
    @Password
    public String password;
    @Roles
    public String role;

    public static void add(String nome, String email, String password, String role) {
        Usuario usuario = new Usuario();
        usuario.nome = nome;
        usuario.email = email;
        usuario.password = BcryptUtil.bcryptHash(password);
        usuario.role = role;
        usuario.persist();
    }
}
