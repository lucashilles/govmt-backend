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
import lucashs.dev.DTOs.LotacaoDTO;
import lucashs.dev.common.PagedList;
import lucashs.dev.entities.Lotacao;
import lucashs.dev.repositories.LotacaoRepository;
import lucashs.dev.repositories.PessoaRepository;
import lucashs.dev.repositories.UnidadeRepository;

@Path("/lotacao")
@Authenticated
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LotacaoResource {

    @Inject
    LotacaoRepository lotacaoRepository;

    @Inject
    PessoaRepository pessoaRepository;

    @Inject
    UnidadeRepository unidadeRepository;

    @GET
    @Path("/all")
    public Response getAll(
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize
    ) {
        Page page = Page.of(pageIndex, pageSize);
        PanacheQuery<Lotacao> pagedQuery = lotacaoRepository.findAll().page(page);

        if (pagedQuery.count() == 0) {
            return Response.ok().build();
        }

        List<LotacaoDTO> dtoList = pagedQuery.list().stream().map(this::toDto).toList();

        PagedList<LotacaoDTO> pagedList = new PagedList<>(dtoList, page.index + 1,
                pagedQuery.pageCount(), page.size, pagedQuery.count());

        return Response.ok(pagedList).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") int id) {
        Lotacao entity = lotacaoRepository.findById(id);
        if (entity == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(toDto(entity)).build();
    }

    @POST
    @Transactional
    public Response createLotacao(LotacaoDTO dto, UriInfo uriInfo) {
        Lotacao entity = fromDto(dto);
        lotacaoRepository.persist(entity);
        URI path = uriInfo.getAbsolutePathBuilder().path(Integer.toString(entity.id)).build();
        return Response.created(path).entity(toDto(entity)).build();
    }


    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateLotacao(@PathParam("id") int id, LotacaoDTO dto, UriInfo uriInfo) {
        Lotacao entity = lotacaoRepository.findById(id);
        if (entity == null) {
            throw new NotFoundException();
        }

        Lotacao updatedEntity = updateFromDto(entity, dto);

        URI path = uriInfo.getAbsolutePathBuilder().path(Integer.toString(updatedEntity.id)).build();
        return Response.created(path).entity(toDto(entity)).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "Admin" })
    @Transactional
    public Response deleteLotacao(@PathParam("id") int id) {
        boolean deleted = lotacaoRepository.deleteById(id);
        return deleted ? Response.noContent().build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }

    private LotacaoDTO toDto(Lotacao entity) {
        LotacaoDTO dto = new LotacaoDTO();
        dto.id = entity.id;
        dto.pessoaId = entity.pessoa.id;
        dto.unidadeId = entity.unidade.id;
        dto.dataLotacao = entity.dataLotacao;
        dto.dataRemocao = entity.dataRemocao;
        dto.portaria = entity.portaria;
        return dto;
    }

    private Lotacao fromDto(LotacaoDTO dto) {
        Lotacao lotacao = new Lotacao();
        return updateFromDto(lotacao, dto);
    }

    private Lotacao updateFromDto(Lotacao entity, LotacaoDTO dto) {
        entity.pessoa = pessoaRepository.findById(dto.pessoaId);
        entity.unidade = unidadeRepository.findById(dto.unidadeId);
        entity.dataLotacao = dto.dataLotacao;
        entity.dataRemocao = dto.dataRemocao;
        entity.portaria = dto.portaria;
        return entity;
    }
}
