package lucashs.dev.resources;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.Map;
import lucashs.dev.DTOs.EnderecoDTO;
import lucashs.dev.DTOs.EnderecoFuncionalDTO;
import lucashs.dev.common.PagedList;
import lucashs.dev.entities.Endereco;
import lucashs.dev.entities.Lotacao;
import lucashs.dev.repositories.CidadeRepository;
import lucashs.dev.repositories.EnderecoRepository;
import lucashs.dev.repositories.LotacaoRepository;
import lucashs.dev.repositories.PessoaRepository;
import lucashs.dev.repositories.UnidadeRepository;
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/endereco")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EnderecoResource {

    @Inject
    EnderecoRepository enderecoRepository;

    @Inject
    CidadeRepository cidadeRepository;

    @Inject
    UnidadeRepository unidadeRepository;

    @Inject
    PessoaRepository pessoaRepository;

    @Inject
    LotacaoRepository lotacaoRepository;

    @GET
    @Path("/all")
    public Response getAll(
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize
    ) {
        Page page = Page.of(pageIndex, pageSize);
        PanacheQuery<Endereco> pagedQuery = enderecoRepository.findAll().page(page);

        if (pagedQuery.count() == 0) {
            return Response.ok().build();
        }

        List<EnderecoDTO> dtoList = pagedQuery.list().stream().map(this::toDTO).toList();

        PagedList<EnderecoDTO> pagedList = new PagedList<>(dtoList, page.index + 1,
                pagedQuery.pageCount(), page.size, pagedQuery.count());

        return Response.ok(pagedList).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") int id) {
        Endereco entity = enderecoRepository.findById(id);
        if (entity == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(toDTO(entity)).build();
    }

    @GET
    @Operation(description = "Busca endereço funcional a partir do nome parcial do servidor efetivo.")
    public Response getByNomeParcialServidorEfetivo(
            @QueryParam("nomeParcial") String nomeParcial,
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize
    ) {
        Page page = Page.of(pageIndex, pageSize);
        PanacheQuery<Lotacao> pagedQuery = lotacaoRepository.findByNomeParcialServidorEfetivo(nomeParcial).page(page);

        if (pagedQuery.list().isEmpty()) {
            return Response.ok().build();
        }

        List<EnderecoFuncionalDTO> dtoList = pagedQuery.list().stream().map(this::toEnderecoFuncionaDTO).toList();

        PagedList<EnderecoFuncionalDTO> pagedList = new PagedList<>(dtoList, page.index + 1,
                pagedQuery.pageCount(), page.size, pagedQuery.count());

        return Response.ok(pagedList).build();
    }

    @POST
    @Transactional
    public Response createEndereco(EnderecoDTO dto, UriInfo uriInfo) {
        Endereco entity = fromDTO(dto);
        enderecoRepository.persist(entity);
        URI path = uriInfo.getAbsolutePathBuilder().path(Integer.toString(entity.id)).build();
        return Response.created(path).entity(toDTO(entity)).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateEndereco(@PathParam("id") int id, EnderecoDTO dto, UriInfo uriInfo) {
        Endereco entity = enderecoRepository.findById(id);
        if (entity == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Endereco updatedEntity = updateFromDTO(entity, dto);

        URI path = uriInfo.getAbsolutePathBuilder().path(Integer.toString(updatedEntity.id))
                .build();
        return Response.created(path).entity(toDTO(entity)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteEndereco(@PathParam("id") int id) {
        boolean deleted = enderecoRepository.deleteById(id);
        return deleted ? Response.noContent().build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }

    private EnderecoFuncionalDTO toEnderecoFuncionaDTO(Lotacao lotacao) {
        EnderecoFuncionalDTO dto = new EnderecoFuncionalDTO();

        dto.servidorEfetivo = Map.of(
                "id", lotacao.pessoa.id,
                "nome", lotacao.pessoa.nome,
                "matricula", lotacao.pessoa.getServidorEfetivo().matricula
        );

        dto.unidade = Map.of(
                "id", lotacao.unidade.id,
                "nome", lotacao.unidade.nome,
                "sigla", lotacao.unidade.sigla,
                "enderecos", lotacao.unidade.enderecos.stream().map(this::toDTO).toArray()
        );

        return dto;
    }

    private EnderecoDTO toDTO(Endereco entity) {
        EnderecoDTO dto = new EnderecoDTO();
        dto.id = entity.id;
        dto.tipoLogradouro = entity.tipoLogradouro;
        dto.logradouro = entity.logradouro;
        dto.numero = entity.numero;
        dto.bairro = entity.bairro;
        dto.cidadeId = entity.cidade.id;
        return dto;
    }

    private Endereco fromDTO(EnderecoDTO dto) {
        Endereco entity = new Endereco();
        return updateFromDTO(entity, dto);
    }

    private Endereco updateFromDTO(Endereco entity, EnderecoDTO dto) {
        entity.tipoLogradouro = dto.tipoLogradouro;
        entity.logradouro = dto.logradouro;
        entity.numero = dto.numero;
        entity.bairro = dto.bairro;
        entity.cidade = cidadeRepository.findById(dto.cidadeId);
        return entity;
    }
}
