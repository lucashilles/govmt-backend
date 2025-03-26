package lucashs.dev.resources;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
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
import java.util.List;
import lucashs.dev.DTOs.ServidorEfetivoRequestDTO;
import lucashs.dev.DTOs.ServidorEfetivoResponseDTO;
import lucashs.dev.common.PagedList;
import lucashs.dev.entities.ServidorEfetivo;
import lucashs.dev.repositories.PessoaRepository;
import lucashs.dev.repositories.ServidorEfetivoRepository;
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/servidor-efetivo")
@Authenticated
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ServidorEfetivoResource {

    @Inject
    PessoaRepository pessoaRepository;

    @Inject
    ServidorEfetivoRepository servidorEfetivoRepository;

    @GET
    @Path("/all")
    public Response getAll(
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize
    ) {
        Page page = Page.of(pageIndex, pageSize);
        PanacheQuery<ServidorEfetivo> pagedQuery = servidorEfetivoRepository.findAll().page(page);

        if (pagedQuery.count() == 0) {
            return Response.ok().build();
        }

        List<ServidorEfetivoResponseDTO> dtoList = pagedQuery.list().stream().map(this::toDto).toList();
        PagedList<ServidorEfetivoResponseDTO> pagedList = new PagedList<>(dtoList, page.index + 1,
                pagedQuery.pageCount(), page.size, pagedQuery.count());

        return Response.ok(pagedList).build();
    }

    @GET
    @Operation(description = "Retorna lista de servidores efetivos para determinada unidade.")
    public Response getAllByUnidade(
            @QueryParam("unidadeId") int unidadeId,
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize
    ) {
        Page page = Page.of(pageIndex, pageSize);
        PanacheQuery<ServidorEfetivo> paged = servidorEfetivoRepository.getByUnidadeId(unidadeId).page(page);

        if (paged.count() == 0) {
            System.out.println("Nenhum encontrado");
            return Response.ok().build();
        }

        List<ServidorEfetivoResponseDTO> dtoList = paged.list().stream().map(this::toDto).toList();
        PagedList<ServidorEfetivoResponseDTO> pagedList = new PagedList<>(dtoList, page.index + 1,
                paged.pageCount(), page.size, paged.count());

        return Response.ok(paged).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(
            @PathParam("id") int id
    ) {
        ServidorEfetivo entity = servidorEfetivoRepository.findById(id);
        if (entity == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(toDto(entity)).build();
    }

    @POST
    @Transactional
    public Response createServidorEfetivo(ServidorEfetivoRequestDTO dto, UriInfo uriInfo) {
        ServidorEfetivo entity = fromDto(dto);
        servidorEfetivoRepository.persist(entity);
        URI path = uriInfo.getAbsolutePathBuilder().path(Integer.toString(entity.id)).build();
        return Response.created(path).entity(toDto(entity)).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateServidorEfetivo(@PathParam("id") int id, ServidorEfetivoRequestDTO dto, UriInfo uriInfo) {
        ServidorEfetivo entity = servidorEfetivoRepository.findById(id);
        if (entity == null) {
            throw new NotFoundException();
        }

        ServidorEfetivo updatedEntity = updateFromDto(entity, dto);

        URI path = uriInfo.getAbsolutePathBuilder().path(Integer.toString(updatedEntity.id)).build();
        return Response.created(path).entity(toDto(entity)).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "Admin" })
    @Transactional
    public Response deleteServidorEfetivo(@PathParam("id") int id) {
        boolean deleted = servidorEfetivoRepository.deleteById(id);

        if (!deleted) {
            throw new NotFoundException();
        }

        return Response.noContent().build();
    }

    private ServidorEfetivoResponseDTO toDto(ServidorEfetivo entity) {
        ServidorEfetivoResponseDTO dto = new ServidorEfetivoResponseDTO();
        dto.id = entity.id;
        dto.matricula = entity.matricula;
        dto.nome = entity.pessoa.nome;
        return dto;
    }

    private ServidorEfetivo fromDto(ServidorEfetivoRequestDTO dto) {
        ServidorEfetivo entity = new ServidorEfetivo();
        return updateFromDto(entity, dto);
    }

    private ServidorEfetivo updateFromDto(ServidorEfetivo entity, ServidorEfetivoRequestDTO dto) {
        entity.matricula = dto.matricula;
        entity.pessoa = pessoaRepository.findById(dto.id);
        return entity;
    }
}
