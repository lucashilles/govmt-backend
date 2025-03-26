package lucashs.dev.resources;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Date;
import java.util.List;
import lucashs.dev.DTOs.FotoPessoaDTO;
import lucashs.dev.common.FileHashing;
import lucashs.dev.common.PagedList;
import lucashs.dev.entities.FotoPessoa;
import lucashs.dev.entities.Pessoa;
import lucashs.dev.repositories.FotoPessoaRepository;
import lucashs.dev.repositories.PessoaRepository;
import lucashs.dev.services.MinioService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;


@Path("/foto-pessoa")
@Authenticated
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FotoPessoaResource {

    @ConfigProperty(name = "minio.bucket-name")
    String bucketName;

    @Inject
    MinioService minioService;

    @Inject
    FotoPessoaRepository fotoPessoaRepository;

    @Inject
    PessoaRepository pessoaRepository;

    private final String basePath = "foto-pessoa/";

    private static final Logger LOG = Logger.getLogger(FotoPessoaResource.class);

    @GET
    @Path("/{id}")
    public Response getDownloadUrl(@PathParam("id") int id) {
        FotoPessoa entity = fotoPessoaRepository.findById(id);

        if (entity == null) {
            return Response.ok().build();
        }

        return Response.ok(getDtoWithUrl(entity)).build();
    }

    @GET
    public Response getDownloadUrl(
            @QueryParam("pessoaId") String pessoaId,
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize
    ) {
        Page page = Page.of(pageIndex, pageSize);
        PanacheQuery<FotoPessoa> pagedQuery = fotoPessoaRepository.find("pessoa IN ?1", pessoaId).page(page);

        if (pagedQuery.count() == 0) {
            return Response.ok().build();
        }
        
        List<FotoPessoaDTO> dtoList = pagedQuery.list().stream().map(this::getDtoWithUrl).toList();
        
        PagedList<FotoPessoaDTO> pagedList = new PagedList<>(dtoList, page.index + 1,
                        pagedQuery.pageCount(), page.size, pagedQuery.count());

        return Response.ok(pagedList).build();
    }

    @POST
    @Path("/{pessoaId}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Transactional
    public Response upload(
            @PathParam("pessoaId") int pessoaId,
            @RestForm("foto") FileUpload file,
            UriInfo uriInfo
    ) {
        if (file == null || !file.contentType().contains("image")) {
            return Response.status(Status.BAD_REQUEST.getStatusCode(),
                    "Field 'foto' must be an image.").build();
        }

        Pessoa pessoa = pessoaRepository.findById(pessoaId);
        if (pessoa == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        String fileHash = "";
        try {
            fileHash = FileHashing.getFileHash(file.filePath());
        } catch (Exception e) {
            LOG.error("Failed to hash uploaded file.", e);
            throw new ServerErrorException(Status.INTERNAL_SERVER_ERROR);
        }

        String fileName = file.fileName();
        String fileExtension = fileName.substring(fileName.lastIndexOf('.'));
        String etag = "";
        try {
            etag = minioService.uploadFile(
                    file.filePath().toString(),basePath + fileHash + fileExtension);
        } catch (Exception e) {
            LOG.error("Failed to uploade file to object storage", e);
            throw new ServerErrorException(Status.INTERNAL_SERVER_ERROR);
        }

        if (etag.isBlank()) {
            LOG.error("Something went wrong when trying to upload file to object storage.\n ==> ID Pessoa: " + pessoaId);
            throw new ServerErrorException(Status.INTERNAL_SERVER_ERROR);
        }

        FotoPessoa fotoPessoa = new FotoPessoa();
        fotoPessoa.bucket = bucketName;
        fotoPessoa.data = new Date();
        fotoPessoa.pessoa = pessoa;
        fotoPessoa.hash = fileHash + fileExtension;

        fotoPessoaRepository.persist(fotoPessoa);

        URI path = uriInfo.getAbsolutePathBuilder().path(Integer.toString(fotoPessoa.id)).build();
        return Response.created(path).entity(getDtoWithUrl(fotoPessoa)).build();
    }

    private FotoPessoaDTO getDtoWithUrl(FotoPessoa fp) {
        String tmpDownloadUrl = "";

        try {
            tmpDownloadUrl = minioService.getTempDownloadUrl(basePath + fp.hash);
        } catch (Exception e) {
            LOG.error("Failed to uploade file to object storage", e);
            throw new ServerErrorException(Status.INTERNAL_SERVER_ERROR);
        }

        return toDto(fp, tmpDownloadUrl);
    }


    private FotoPessoaDTO toDto(FotoPessoa entity, String url) {
        FotoPessoaDTO dto = new FotoPessoaDTO();
        dto.id = entity.id;
        dto.pessoaId = entity.pessoa.id;
        dto.data = entity.data;
        dto.url = url;
        return dto;
    }
}
