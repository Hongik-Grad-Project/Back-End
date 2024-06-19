package trackers.demo.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import trackers.demo.global.exception.ExceptionCode;
import trackers.demo.global.exception.ImageException;
import trackers.demo.image.domain.ImageFile;
import trackers.demo.image.domain.S3ImageEvent;
import trackers.demo.image.infrastructure.ImageUploader;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageService {

    private final ImageUploader imageUploader;
    private final ApplicationEventPublisher publisher;

    public String save(final MultipartFile mainImage) {
        final ImageFile imageFile = new ImageFile(mainImage);
        return uploadImage(imageFile);
    }

    private String uploadImage(final ImageFile imageFile) {
        try{
            final String uploadedImageName = imageUploader.uploadImage(imageFile);
            if(uploadedImageName.isEmpty()){
                throw new ImageException(ExceptionCode.INVALID_IMAGE_PATH);
            }
            return uploadedImageName;
        } catch (final ImageException e){
            publisher.publishEvent(new S3ImageEvent(imageFile.getHashedName()));
            throw e;
        }
    }
}
