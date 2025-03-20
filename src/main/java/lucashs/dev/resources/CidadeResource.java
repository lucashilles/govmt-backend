package lucashs.dev.resources;


import io.quarkus.panache.common.Page;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
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
    CidadeRepository cidadeService;

    @GET
    @Path("/{id}")
    public Response getCidadeById(@PathParam("id") int id) {
        return Response.ok(cidadeService.findById(id)).build();
    }

    @GET
    public Response findCidadeByNome(
            @QueryParam("nome") String nome,
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize
    ) {
        PagedList<Cidade> paginatedList = validateAndFind(nome, pageIndex, pageSize);
        return Response.ok(paginatedList).build();
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public Response findCidadeBy(
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize,
            String nome
    ) {
        PagedList<Cidade> paginatedList = validateAndFind(nome, pageIndex, pageSize);
        return Response.ok(paginatedList).build();
    }

    private PagedList<Cidade> validateAndFind(String nome, int pageIndex, int pageSize) {
        if (nome == null || nome.isBlank()) {
            throw new BadRequestException("Parâmetro 'nome' deve ser fornecido.");
        }

        Page page = Page.of(pageIndex, pageSize);
        return cidadeService.findByNome(nome, page);
    }
}
