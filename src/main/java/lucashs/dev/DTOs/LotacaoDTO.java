package lucashs.dev.DTOs;

import java.time.LocalDate;

public class LotacaoDTO {
    public int id;
    public int pessoaId;
    public int unidadeId;
    public LocalDate dataLotacao;
    public LocalDate dataRemocao;
    public String portaria;
}
