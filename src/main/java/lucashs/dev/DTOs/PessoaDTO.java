package lucashs.dev.DTOs;

import java.util.Date;
import java.util.Set;

public class PessoaDTO {
    public int id;
    public String nome;
    public Date dataNascimento;
    public String sexo;
    public String mae;
    public String pai;
    public Set<Integer> enderecoIds;
}
