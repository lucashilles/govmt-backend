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
import lucashs.dev.DTOs.ServidorTemporarioRequestDTO;
import lucashs.dev.DTOs.ServidorTemporarioResponseDTO;
import lucashs.dev.common.PagedList;
import lucashs.dev.entities.ServidorTemporario;
import lucashs.dev.repositories.PessoaRepository;
import lucashs.dev.repositories.ServidorTemporarioRepository;

@Path("/servidor-temporario")
@Authenticated
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ServidorTemporarioResource {

    @Inject
    PessoaRepository pessoaRepository;

    @Inject
    ServidorTemporarioRepository servidorTemporarioRepository;

    @GET
    @Path("/all")
    public Response getAll(
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize
    ) {
        Page page = Page.of(pageIndex, pageSize);
        PanacheQuery<ServidorTemporario> pagedQuery = servidorTemporarioRepository.findAll()
                .page(page);

        if (pagedQuery.count() == 0) {
            return Response.ok().build();
        }

        List<ServidorTemporarioResponseDTO> dtoList = pagedQuery.list().stream().map(this::toDto)
                .toList();

        PagedList<ServidorTemporarioResponseDTO> pagedList = new PagedList<>(dtoList,
                page.index + 1, pagedQuery.pageCount(), page.size, pagedQuery.count());

        return Response.ok(pagedList).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") int id) {
        ServidorTemporario entity = servidorTemporarioRepository.findById(id);
        if (entity == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(toDto(entity)).build();
    }

    @POST
    @Transactional
    public Response createServidorTemporario(ServidorTemporarioRequestDTO dto, UriInfo uriInfo) {
        ServidorTemporario entity = fromDto(dto);
        servidorTemporarioRepository.persist(entity);
        URI path = uriInfo.getAbsolutePathBuilder().path(Integer.toString(entity.id)).build();
        return Response.created(path).entity(toDto(entity)).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateServidorTemporario(@PathParam("id") int id, ServidorTemporarioRequestDTO dto, UriInfo uriInfo) {
        ServidorTemporario entity = servidorTemporarioRepository.findById(id);
        if (entity == null) {
            throw new NotFoundException();
        }

        ServidorTemporario updatedEntity = updateFromDto(entity, dto);

        URI path = uriInfo.getAbsolutePathBuilder().path(Integer.toString(updatedEntity.id)).build();
        return Response.created(path).entity(toDto(updatedEntity)).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "Admin" })
    @Transactional
    public Response deleteServidorTemporario(@PathParam("id") int id) {
        boolean deleted = servidorTemporarioRepository.deleteById(id);

        if (!deleted) {
            throw new NotFoundException();
        }

        return Response.noContent().build();
    }

    private ServidorTemporarioResponseDTO toDto(ServidorTemporario entity) {
        ServidorTemporarioResponseDTO dto = new ServidorTemporarioResponseDTO();
        dto.id = entity.id;
        dto.nome = entity.pessoa.nome;
        dto.dataAdmissao = entity.dataAdmissao;
        dto.dataDemissao = entity.dataDemissao;
        return dto;
    }

    private ServidorTemporario fromDto(ServidorTemporarioRequestDTO dto) {
        ServidorTemporario entity = new ServidorTemporario();
        return updateFromDto(entity, dto);
    }

    private ServidorTemporario updateFromDto(ServidorTemporario entity, ServidorTemporarioRequestDTO dto) {
        entity.dataAdmissao = dto.dataAdmissao;
        entity.dataDemissao = dto.dataDemissao;
        entity.pessoa = pessoaRepository.findById(dto.id);
        return entity;
    }
}
