package live.dgrr.global.config.jwt;

import live.dgrr.global.config.yml.YamlPropertySourceFactory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Getter
@Setter
@PropertySource(value= "classpath:/properties/jwt.yml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    private String secret;
}
