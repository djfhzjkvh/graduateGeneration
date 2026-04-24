package com.graduation.crm.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI 文档配置。
 */
@Slf4j
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI graduationOpenAPI() {
        log.info("Initializing OpenAPI document configuration");
        return new OpenAPI()
                .info(new Info()
                        .title("客户购买意向跟进提醒系统 API")
                        .description("毕业设计后端接口文档，支持在线查看和调试接口。")
                        .version("0.0.1")
                        .contact(new Contact().name("graduate-generation"))
                        .license(new License().name("Internal Use")));
    }
}
