package tidify.tidify.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = {"tidify.tidify.oauth"})
public class FeignConfig {
}


