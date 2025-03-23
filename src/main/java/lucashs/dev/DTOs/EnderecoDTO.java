package lucashs.dev.DTOs;

import java.util.Set;

public class EnderecoDTO {
    public int id;
    public String tipoLogradouro;
    public String logradouro;
    public int numero;
    public String bairro;
    public int cidadeId;
    public Set<Integer> unidadeIds;
    public Set<Integer> pessoaIds;
}
