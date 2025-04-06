package lucashs.dev.resources;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
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
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import lucashs.dev.DTOs.PessoaDTO;
import lucashs.dev.common.PagedList;
import lucashs.dev.entities.Pessoa;
import lucashs.dev.repositories.EnderecoRepository;
import lucashs.dev.repositories.PessoaRepository;

@Path("/pessoa")
@Authenticated
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PessoaResource {

    @Inject
    EnderecoRepository enderecoRepository;

    @Inject
    PessoaRepository pessoaRepository;

    @GET
    @Path("/all")
    public Response getAll(
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize
    ) {
        Page page = Page.of(pageIndex, pageSize);
        PanacheQuery<Pessoa> pagedQuery = pessoaRepository.findAll().page(page);

        if (pagedQuery.count() == 0) {
            return Response.ok().build();
        }

        List<PessoaDTO> dtoList = pagedQuery.list().stream().map(this::toDto).toList();

        PagedList<PessoaDTO> pagedList = new PagedList<>(dtoList, page.index + 1,
                pagedQuery.pageCount(), page.size, pagedQuery.count());

        return Response.ok(pagedList).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(
            @PathParam("id") int id
    ) {
        Pessoa entity = pessoaRepository.findById(id);
        if (entity == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(toDto(entity)).build();
    }

    @POST
    @Transactional
    public Response createPessoa(PessoaDTO dto, UriInfo uriInfo) {
        if (LocalDate.now().isBefore(dto.dataNascimento)) {
            throw new BadRequestException("Data de nascimento inválida.");
        }

        Pessoa entity = fromDto(dto);
        pessoaRepository.persist(entity);
        URI path = uriInfo.getAbsolutePathBuilder().path(Integer.toString(entity.id)).build();
        return Response.created(path).entity(toDto(entity)).build();
    }


    @PUT
    @Path("/{id}")
    @Transactional
    public Response updatePessoa(@PathParam("id") int id, PessoaDTO dto, UriInfo uriInfo) {
        Pessoa entity = pessoaRepository.findById(id);
        if (entity == null) {
            throw new NotFoundException();
        }

        if (LocalDate.now().isBefore(dto.dataNascimento)) {
            throw new BadRequestException("Data de nascimento inválida.");
        }

        Pessoa updatedEntity = updateFromDto(entity, dto);

        URI path = uriInfo.getAbsolutePathBuilder().path(Integer.toString(updatedEntity.id)).build();
        return Response.created(path).entity(toDto(entity)).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "Admin" })
    @Transactional
    public Response deletePessoa(@PathParam("id") int id) {
        boolean deleted = pessoaRepository.deleteById(id);
        return deleted ? Response.noContent().build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }

    private PessoaDTO toDto(Pessoa entity) {
        PessoaDTO dto = new PessoaDTO();
        dto.id = entity.id;
        dto.nome = entity.nome;
        dto.dataNascimento = entity.dataNascimento;
        dto.sexo = entity.sexo;
        dto.mae = entity.mae;
        dto.pai = entity.pai;
        dto.enderecoIds = entity.enderecos.stream().map(e -> e.id).collect(Collectors.toSet());
        return dto;
    }

    private Pessoa fromDto(PessoaDTO dto) {
        Pessoa lotacao = new Pessoa();
        return updateFromDto(lotacao, dto);
    }

    private Pessoa updateFromDto(Pessoa entity, PessoaDTO dto) {
        entity.nome = dto.nome;
        entity.dataNascimento = dto.dataNascimento;
        entity.sexo = dto.sexo;
        entity.mae = dto.mae;
        entity.pai = dto.pai;
        entity.enderecos = new HashSet<>(enderecoRepository.list("id IN ?1", dto.enderecoIds));
        return entity;
    }
}
