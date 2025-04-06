package lucashs.dev.DTOs;

import java.time.LocalDate;
import java.util.Set;

public class PessoaDTO {
    public int id;
    public String nome;
    public LocalDate dataNascimento;
    public String sexo;
    public String mae;
    public String pai;
    public Set<Integer> enderecoIds;
}
