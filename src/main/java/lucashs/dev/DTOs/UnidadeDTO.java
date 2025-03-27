package lucashs.dev.DTOs;

import java.util.Set;
import java.util.stream.Collectors;
import lucashs.dev.entities.Unidade;

public class UnidadeDTO {
    public int id;
    public String nome;
    public String sigla;
    public Set<Integer> enderecoIds;

    public UnidadeDTO(Unidade unidade) {
        id = unidade.id;
        nome = unidade.nome;
        sigla = unidade.sigla;
        enderecoIds = unidade.enderecos.stream().map(e -> e.id).collect(Collectors.toSet());
    }
}
