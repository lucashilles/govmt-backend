package lucashs.dev.resources;


import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
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
    public Response getCidadeById(@PathParam("id") int id) {
        return Response.ok(cidadeRepository.findById(id)).build();
    }

    @GET
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
}
