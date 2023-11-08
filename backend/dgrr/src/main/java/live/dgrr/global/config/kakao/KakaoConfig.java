package live.dgrr.global.config.kakao;

import live.dgrr.global.config.yml.YamlPropertySourceFactory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Getter
@Setter
@PropertySource(value= "classpath:/properties/kakao.yml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "kakao")
public class KakaoConfig {
    private String clientId;
    private String redirectUri;
}
