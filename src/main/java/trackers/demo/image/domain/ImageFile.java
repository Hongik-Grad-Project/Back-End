package trackers.demo.image.domain;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;
import trackers.demo.global.exception.ExceptionCode;
import trackers.demo.global.exception.ImageException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.springframework.util.StringUtils.getFilenameExtension;
import static trackers.demo.global.exception.ExceptionCode.*;

@Getter
public class ImageFile {

    private static final String EXTENSION_DELIMITER = ".";

    private final MultipartFile file;
    private final String hashedName;

    public ImageFile(final MultipartFile file){
        validateNullImage(file);
        this.file= file;
        this.hashedName = hashName(file);
    }

    private void validateNullImage(final MultipartFile file) {
        if(file.isEmpty()){
            throw new ImageException(NULL_IMAGE);
        }
    }

    private String hashName(final MultipartFile image) {
        final String name = image.getOriginalFilename();
        final String filenameExtension = EXTENSION_DELIMITER + getFilenameExtension(name);
        final String nameAndDate = name + LocalDateTime.now();
        try{
            // 해시 알고리즘 객체 생성
            final MessageDigest hashAlgorithm = MessageDigest.getInstance("SHA3-256");
            // 문자열을 바이트 배열로 변환하여 해시값 생성
            final byte[] hashBytes = hashAlgorithm.digest(nameAndDate.getBytes(StandardCharsets.UTF_8));
            // 해시값을 16진수 문자열로 변환하고 파일 확장자를 붙여 최종 파일명 생성
            return bytesToHex(hashBytes) + filenameExtension;
        } catch(final NoSuchAlgorithmException e){
            throw new ImageException(FAIL_IMAGE_NAME_HASH);
        }
    }

    private String bytesToHex(final byte[] bytes) {
        return IntStream.range(0, bytes.length)
                // 각 바이트 값을 16진수 문자열로 변환
                .mapToObj(i -> String.format("%02x", bytes[i] & 0xff))
                .collect(Collectors.joining());
    }

    public String getContentType(){ return this.file.getContentType();}

    public long getSize(){ return this.file.getSize(); }

    public InputStream getInputStream() throws IOException {
        return this.file.getInputStream();
    }
}
