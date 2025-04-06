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
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lucashs.dev.DTOs.ServidorDTO;
import lucashs.dev.DTOs.ServidorEfetivoRequestDTO;
import lucashs.dev.DTOs.ServidorEfetivoResponseDTO;
import lucashs.dev.DTOs.UnidadeDTO;
import lucashs.dev.common.PagedList;
import lucashs.dev.entities.FotoPessoa;
import lucashs.dev.entities.ServidorEfetivo;
import lucashs.dev.entities.Unidade;
import lucashs.dev.repositories.FotoPessoaRepository;
import lucashs.dev.repositories.PessoaRepository;
import lucashs.dev.repositories.ServidorEfetivoRepository;
import lucashs.dev.repositories.UnidadeRepository;
import lucashs.dev.services.FotoPessoaService;
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/servidor-efetivo")
@Authenticated
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ServidorEfetivoResource {

    @Inject
    FotoPessoaRepository fotoPessoaRepository;

    @Inject
    FotoPessoaService fotoPessoaService;

    @Inject
    PessoaRepository pessoaRepository;

    @Inject
    ServidorEfetivoRepository servidorEfetivoRepository;

    @Inject
    UnidadeRepository unidadeRepository;

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
            @QueryParam("unid_id") int unidadeId,
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize
    ) {
        Unidade unidade = unidadeRepository.findById(unidadeId);
        if (unidade == null) {
            throw new NotFoundException("Unidade não encontrada.");
        }

        Page page = Page.of(pageIndex, pageSize);
        PanacheQuery<ServidorEfetivo> paged = servidorEfetivoRepository.getByUnidadeId(unidadeId).page(page);

        if (paged.count() == 0) {
            throw new NotFoundException("Nenhum servidor efetivo encontrado lotado nesta unidade.");
        }

        List<ServidorDTO> servidores = paged.list().stream().map(se -> getServidorDTO(se, unidade)).toList();

        PagedList<ServidorDTO> pagedList = new PagedList<>(servidores, page.index + 1,
                paged.pageCount(), page.size, paged.count());

        return Response.ok(pagedList).build();
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

    private ServidorDTO getServidorDTO(ServidorEfetivo se, Unidade unidade) {
        ServidorDTO servidorDTO = new ServidorDTO();
        servidorDTO.unidade = new UnidadeDTO(unidade);
        servidorDTO.nome = se.pessoa.nome;
        servidorDTO.idade = calculateAge(se.pessoa.dataNascimento);
        servidorDTO.foto = getPhotos(se.id);
        return servidorDTO;
    }

    public int calculateAge(LocalDate dataNasc) {
        LocalDate dataAtual = LocalDate.now();
        return (int) ChronoUnit.YEARS.between(dataNasc, dataAtual);
    }

    private List<String> getPhotos(int pessoaId) {
        List<FotoPessoa> fotoPessoas = fotoPessoaRepository.find("pessoa.id", pessoaId).list();
        return fotoPessoas.stream()
                .map(fp -> fotoPessoaService.getTempDownloadUrl(fp.hash))
                .toList();
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
