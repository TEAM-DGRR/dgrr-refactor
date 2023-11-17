package live.dgrr.global.config.redis;

import live.dgrr.global.config.yml.YamlPropertySourceFactory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Getter
@Setter
@PropertySource(value= "classpath:/properties/redis.yml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "ranking")
public class RankingConfig {
    private String key;
    private int season;
}
