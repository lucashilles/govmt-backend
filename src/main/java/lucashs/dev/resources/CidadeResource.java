package lucashs.dev.resources;


import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.annotation.security.PermitAll;
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
import lucashs.dev.common.PagedList;
import lucashs.dev.entities.Cidade;
import lucashs.dev.repositories.CidadeRepository;


@Path("/cidade")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CidadeResource {

    @Inject
    CidadeRepository cidadeRepository;

    @GET
    @Path("/all")
    @PermitAll
    public Response getAll(
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize
    ) {
        Page page = Page.of(pageIndex, pageSize);
        PanacheQuery<Cidade> paged = cidadeRepository.findAll().page(page);

        if (paged.count() == 0) {
            return Response.ok().build();
        }

        PagedList<Cidade> pagedList = new PagedList<>(paged.list(), page.index + 1,
                paged.pageCount(), page.size, paged.count());

        return Response.ok(pagedList).build();
    }

    @GET
    @Path("/{id}")
    @PermitAll
    public Response getCidadeById(@PathParam("id") int id) {
        return Response.ok(cidadeRepository.findById(id)).build();
    }

    @GET
    @PermitAll
    public Response findCidadeByNome(
            @QueryParam("nome") String nome,
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize
    ) {
        Page page = Page.of(pageIndex, pageSize);
        PagedList<Cidade> paged = cidadeRepository.findByNome(nome, page);

        if (paged.size == 0) {
            return Response.ok().build();
        }

        return Response.ok(paged).build();
    }

    @POST
    @RolesAllowed({ "Admin" })
    @Transactional
    public Response createCidade(Cidade entity, UriInfo uriInfo) {
        cidadeRepository.persist(entity);
        URI path = uriInfo.getAbsolutePathBuilder().path(Integer.toString(entity.id)).build();
        return Response.created(path).entity(entity).build();
    }


    @PUT
    @Path("/{id}")
    @RolesAllowed({ "Admin" })
    @Transactional
    public Response updateCidade(@PathParam("id") int id, Cidade dto, UriInfo uriInfo) {
        Cidade entity = cidadeRepository.findById(id);
        if (entity == null) {
            throw new NotFoundException();
        }

        entity.nome = dto.nome;
        entity.uf = dto.uf;

        URI path = uriInfo.getAbsolutePathBuilder().path(Integer.toString(id)).build();
        return Response.created(path).entity(entity).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "Admin" })
    @Transactional
    public Response deleteCidade(@PathParam("id") int id) {
        boolean deleted = cidadeRepository.deleteById(id);
        return deleted ? Response.noContent().build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }
}
