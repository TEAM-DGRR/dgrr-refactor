package live.dgrr.global.config.yml;

import live.dgrr.global.exception.ErrorCode;
import live.dgrr.global.exception.GeneralException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;
import java.util.Properties;

@Slf4j
public class YamlPropertySourceFactory implements PropertySourceFactory {
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(resource.getResource());
        try {
            Properties properties = factory.getObject();
            return new PropertiesPropertySource(resource.getResource().getFilename(), properties);
        } catch (Exception e) {
//            log.error("Configuration resource ({}) can't be loaded", resource.getResource().getURI());
            throw new GeneralException(ErrorCode.FILE_NOT_FOUND, e);
        }
    }
}
