package es.upm.dit.isst.grupo10.urbanactive.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Le decimos a Spring: "Cuando alguien pida algo que empiece por /uploads/..."
        // "...búscalo físicamente en la carpeta 'uploads' que está en la raíz de mi proyecto"
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}