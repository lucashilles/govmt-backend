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
import lucashs.dev.DTOs.EnderecoDTO;
import lucashs.dev.common.PagedList;
import lucashs.dev.entities.Endereco;
import lucashs.dev.repositories.CidadeRepository;
import lucashs.dev.repositories.EnderecoRepository;
import lucashs.dev.repositories.PessoaRepository;
import lucashs.dev.repositories.UnidadeRepository;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

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

    @GET
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

    private EnderecoDTO toDTO(Endereco entity) {
        EnderecoDTO dto = new EnderecoDTO();
        dto.id = entity.id;
        dto.tipoLogradouro = entity.tipoLogradouro;
        dto.logradouro = entity.logradouro;
        dto.numero = entity.numero;
        dto.bairro = entity.bairro;
        dto.cidadeId = entity.cidade.id;
        dto.unidadeIds = entity.unidades.stream().map(u -> u.id).collect(Collectors.toSet());
        dto.pessoaIds = entity.pessoas.stream().map(p -> p.id).collect(Collectors.toSet());
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
        entity.unidades = new HashSet<>(unidadeRepository.list("id IN ?1", dto.unidadeIds));
        entity.pessoas = new HashSet<>(pessoaRepository.list("id IN ?1", dto.pessoaIds));
        return entity;
    }
}
