package lucashs.dev.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.core.Response.Status;
import lucashs.dev.common.FileHashing;
import lucashs.dev.resources.FotoPessoaResource;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.multipart.FileUpload;

@ApplicationScoped
public class FotoPessoaService {

    @Inject
    MinioService minioService;

    private final String basePath = "foto-pessoa/";

    private static final Logger LOG = Logger.getLogger(FotoPessoaResource.class);

    public String uploadFile(FileUpload file) {
        String fileHashName = getFileHashName(file);

        try {
            String etag = minioService.uploadFile(
                    file.filePath().toString(),basePath + fileHashName);

            if (etag.isBlank()) {
                LOG.error("Something went wrong when trying to upload file to object storage.");
                throw new ServerErrorException(Status.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            LOG.error("Failed to uploade file to object storage", e);
            throw new ServerErrorException(Status.INTERNAL_SERVER_ERROR);
        }

        return fileHashName;
    }

    private String getFileHashName(FileUpload file) {
        String fileHash = "";
        try {
            fileHash = FileHashing.getFileHash(file.filePath());
        } catch (Exception e) {
            LOG.error("Failed to hash uploaded file.", e);
            throw new ServerErrorException(Status.INTERNAL_SERVER_ERROR);
        }

        String fileName = file.fileName();
        String fileExtension = fileName.substring(fileName.lastIndexOf('.'));
        return fileHash + fileExtension;
    }

    public String getTempDownloadUrl(String filename) {
        String file = "";

        try {
            file = minioService.getTempDownloadUrl(basePath + filename);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return file;
    }

}
