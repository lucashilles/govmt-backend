package lucashs.dev.resources;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
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
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import lucashs.dev.DTOs.UnidadeDTO;
import lucashs.dev.common.PagedList;
import lucashs.dev.entities.Unidade;
import lucashs.dev.repositories.EnderecoRepository;
import lucashs.dev.repositories.UnidadeRepository;

@Path("/unidade")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UnidadeResource {

    @Inject
    EnderecoRepository enderecoRepository;

    @Inject
    UnidadeRepository unidadeRepository;

    @GET
    @Path("/all")
    public Response getAll(
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize
    ) {
        Page page = Page.of(pageIndex, pageSize);
        PanacheQuery<Unidade> pagedQuery = unidadeRepository.findAll().page(page);

        if (pagedQuery.count() == 0) {
            return Response.ok().build();
        }

        List<UnidadeDTO> dtoList = pagedQuery.list().stream().map(this::toDto).toList();

        PagedList<UnidadeDTO> pagedList = new PagedList<>(dtoList, page.index + 1,
                pagedQuery.pageCount(), page.size, pagedQuery.count());

        return Response.ok(pagedList).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(
            @PathParam("id") int id
    ) {
        Unidade entity = unidadeRepository.findById(id);
        if (entity == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(toDto(entity)).build();
    }

    @POST
    @Transactional
    public Response createUnidade(UnidadeDTO dto, UriInfo uriInfo) {
        Unidade entity = fromDto(dto);
        unidadeRepository.persist(entity);
        URI path = uriInfo.getAbsolutePathBuilder().path(Integer.toString(entity.id)).build();
        return Response.created(path).entity(toDto(entity)).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateUnidade(@PathParam("id") int id, UnidadeDTO dto, UriInfo uriInfo) {
        Unidade entity = unidadeRepository.findById(id);
        if (entity == null) {
            throw new NotFoundException();
        }

        Unidade updatedEntity = updateFromDto(entity, dto);

        URI path = uriInfo.getAbsolutePathBuilder().path(Integer.toString(updatedEntity.id)).build();
        return Response.created(path).entity(toDto(entity)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteUnidade(@PathParam("id") int id) {
        boolean deleted = unidadeRepository.deleteById(id);

        if (!deleted) {
            throw new NotFoundException();
        }

        return Response.noContent().build();
    }

    private UnidadeDTO toDto(Unidade entity) {
        UnidadeDTO dto = new UnidadeDTO();
        dto.id = entity.id;
        dto.nome = entity.nome;
        dto.sigla = entity.sigla;
        dto.enderecoIds = entity.enderecos.stream().map(e -> e.id).collect(Collectors.toSet());
        return dto;
    }

    private Unidade fromDto(UnidadeDTO dto) {
        Unidade entity = new Unidade();
        return updateFromDto(entity, dto);
    }

    private Unidade updateFromDto(Unidade entity, UnidadeDTO dto) {
        entity.nome = dto.nome;
        entity.sigla = dto.sigla;
        entity.enderecos = new HashSet<>(enderecoRepository.list("id IN ?1", dto.enderecoIds));
        return entity;
    }
}
