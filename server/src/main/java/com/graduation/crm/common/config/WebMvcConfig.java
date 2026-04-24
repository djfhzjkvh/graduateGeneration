package com.graduation.crm.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final FileProperties fileProperties;

    /**
     * 将本地上传目录映射为静态资源路径，方便前端预览图片和附件。
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = "file:" + normalizeDir(fileProperties.getUploadDir());
        registry.addResourceHandler(fileProperties.getAccessPrefix() + "/**")
                .addResourceLocations(location);
    }

    private String normalizeDir(String dir) {
        String normalized = dir.replace("\\", "/");
        return normalized.endsWith("/") ? normalized : normalized + "/";
    }
}

