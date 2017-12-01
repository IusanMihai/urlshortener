package com.mihai.shorturl.api;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(Predicates.not(PathSelectors.regex("/error")))
                .build().apiInfo(apiInfo());
    }

    @Bean
    public UiConfiguration uiConfiguration() {
        return new UiConfiguration("", "list", "alpha", "schema", null, false, false, null);
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "URL shortening REST API",
                "This API is used for shortening URLs",
                "1.0",
                "Terms of service",
                new Contact("Mihai Iusan", "swagger-ui.html", "mihai.iusan@gmail.com"),
                "License of API",
                "https://opensource.org/licenses/MIT");
    }
}
