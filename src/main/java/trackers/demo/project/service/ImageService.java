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

import java.util.List;

import static trackers.demo.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageService {

    private static final int MAX_IMAGE_LIST_SIZE = 5;
    private static final int EMPTY_LIST_SIZE = 0;

    private final ImageUploader imageUploader;
    private final ApplicationEventPublisher publisher;

    public String saveImage(final MultipartFile mainImage) {
        // 이미지 유효성 검증
        validateSizeOfImage(mainImage);
        // 이미지 파일 객체 생성
        final ImageFile imageFile = new ImageFile(mainImage);
        return uploadImage(imageFile);   // 해시된 이미지 이름
    }

    public List<String> saveImages(final List<MultipartFile> images) {
        validateSizeOfImages(images);
        final List<ImageFile> imageFiles = images.stream()
                .map(ImageFile::new)
                .toList();
        return uploadImages(imageFiles);    // 해시된 이미지 이름
    }

    private String uploadImage(final ImageFile imageFile) {
        try{
            final String uploadedImageName = imageUploader.uploadImage(imageFile);
            if(uploadedImageName.isEmpty()){
                throw new ImageException(INVALID_IMAGE_PATH);
            }
            return uploadedImageName;
        } catch (final ImageException e){
            publisher.publishEvent(new S3ImageEvent(imageFile.getHashedName()));
            throw e;
        }
    }

    private List<String> uploadImages(final List<ImageFile> imageFiles) {
        try {
            final List<String> uploadedImageNames = imageUploader.uploadImages(imageFiles);
            if(uploadedImageNames.size() != imageFiles.size()){
                throw new ImageException(INVALID_IMAGE_PATH);
            }
            return uploadedImageNames;
        } catch (final ImageException e){
            imageFiles.forEach(imageFile -> publisher.publishEvent(new S3ImageEvent(imageFile.getHashedName())));
            throw e;
        }
    }

    private void validateSizeOfImage(final MultipartFile image) {
        if(image == null || image.isEmpty()){
            throw new ImageException(EMPTY_IMAGE);
        }
    }

    private void validateSizeOfImages(final List<MultipartFile> images) {
        if(images.size() > MAX_IMAGE_LIST_SIZE){
            throw new ImageException(EXCEED_IMAGE_LIST_SIZE);
        }
    }


}
