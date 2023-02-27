package tidify.tidify.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@EnableWebMvc
public class SwaggerConfig {
    private String version = "V0.1";

    // @Bean
    // public Docket api() {
    //     return new Docket(DocumentationType.SWAGGER_2)
    //         .select()
    //         .apis(RequestHandlerSelectors.any())
    //         .paths(PathSelectors.any())
    //         .build()
    //         .apiInfo(apiInfo());
    // }
    //
    // private ApiInfo apiInfo() {
    //     return new ApiInfoBuilder()
    //         .title("제목")
    //         .description("설명")
    //         .version(version)
    //         .contact(new Contact("이름", "홈페이지 URL", "e-mail"))
    //         .build();
    // }
}