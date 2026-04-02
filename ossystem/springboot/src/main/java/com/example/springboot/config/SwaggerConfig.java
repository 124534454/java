package com.example.springboot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    /**
     * 自定义 OpenAPI 文档信息
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // 文档基本信息
                .info(new Info()
                        .title("接口文档")  // 标题
                        .description("Restful 接口文档")  // 描述
                        .version("1.0")  // 版本
                        // 联系人信息
                        .contact(new Contact()
                                .name("xxx")
                                .email("xxx@qq.com")
                        )
                );
    }
}