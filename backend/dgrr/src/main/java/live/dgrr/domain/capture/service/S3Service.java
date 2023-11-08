package live.dgrr.domain.capture.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import live.dgrr.global.exception.GameException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import live.dgrr.global.exception.ErrorCode;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public URL uploadBase64EncodedImage(String base64EncodedImage) {
        byte[] imageBytes = Base64.getDecoder().decode(base64EncodedImage);
        String fileName = UUID.randomUUID().toString();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imageBytes.length);
        metadata.setContentType("image/jpeg");

        try (InputStream inputStream = new ByteArrayInputStream(imageBytes)) {
            amazonS3.putObject(bucket, fileName, inputStream, metadata);
            return amazonS3.getUrl(bucket, fileName);
        } catch (IOException e) {
            throw new GameException(ErrorCode.IMAGE_UPLOAD_ERROR, e);
        }
    }

}