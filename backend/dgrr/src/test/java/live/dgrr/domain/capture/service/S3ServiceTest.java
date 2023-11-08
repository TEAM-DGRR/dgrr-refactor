package live.dgrr.domain.capture.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class S3ServiceTest {

    @Autowired
    private S3Service s3Service;
    @DisplayName("S3 이미지 업로드 테스트")
    @Test
    void realUploadTest() {

        ClassPathResource resource = new ClassPathResource("images/happy.png");

        try (InputStream inputStream = resource.getInputStream()) {
            // 파일을 바이트 배열로 읽어들임
            byte[] imageBytes = inputStream.readAllBytes();

            // 바이트 배열을 Base64 인코딩된 문자열로 변환
            String encodedImage = Base64.getEncoder().encodeToString(imageBytes);

            // 인코딩된 문자열을 함수에 전달
            URL uploadedImageUrl = s3Service.uploadBase64EncodedImage(encodedImage);

            assertNotNull(uploadedImageUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
