package trackers.demo.image.infrastructure;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import trackers.demo.global.exception.ExceptionCode;
import trackers.demo.global.exception.ImageException;
import trackers.demo.image.domain.ImageFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImageUploader {

    private static final String CACHE_CONTROL_VALUE = "max-age=3153600";

    private final AmazonS3 s3Client;

    // trackers-aurora-dev-bucket
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.project-folder}")
    private String projectFolder;

    @Value("${cloud.aws.s3.profile-folder}")
    private String profileFolder;


    @Value("${cloud.aws.s3.cloud-front-project-domain}")
    private String cloudFrontProjectDomain;

    public List<String> uploadImages(final List<ImageFile> imageFiles){
        final List<CompletableFuture<String>> imageUploadsFuture = imageFiles.stream()
                .map(imageFile -> CompletableFuture.supplyAsync(() -> uploadImage(imageFile)))
                .toList();
        return getUploadedImageNamesFromFutures(imageUploadsFuture);
    }

    private List<String> getUploadedImageNamesFromFutures(List<CompletableFuture<String>> futures) {
        final List<String> fileNames = new ArrayList<>();
        futures.forEach(future -> {
            try {
                fileNames.add(future.join());
            } catch (final CompletionException ignored){}
        });
        return fileNames;
    }

    public String uploadImage(final ImageFile imageFile){
        final String path = projectFolder + imageFile.getHashedName();

        final ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(imageFile.getContentType());
        metadata.setContentLength(imageFile.getSize());
        metadata.setCacheControl(CACHE_CONTROL_VALUE);

        try(final InputStream inputStream = imageFile.getInputStream()){
            s3Client.putObject(bucket, path, inputStream, metadata);
            String objectURL = "https://" + cloudFrontProjectDomain + "/" + path;
            return objectURL;
        } catch (AmazonServiceException e ){
            throw new ImageException(ExceptionCode.INVALID_IMAGE_PATH);
        } catch (final IOException e){
            throw new ImageException(ExceptionCode.INVALID_IMAGE);
        }
    }

}
