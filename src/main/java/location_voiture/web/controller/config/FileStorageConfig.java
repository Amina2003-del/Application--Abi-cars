package location_voiture.web.controller.config;

import org.springframework.beans.factory.annotation.Value;

//FileStorageConfig.java

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileStorageConfig implements WebMvcConfigurer {
	 @Value("${file.upload-dir}")
	 private String uploadDirs;
 @Override
 public void addResourceHandlers(ResourceHandlerRegistry registry) {
 
     registry.addResourceHandler("/uploads/**")
     .addResourceLocations("file:" + uploadDirs + "/");
 }

/* Path uploadDir = Paths.get("src/main/resources/static/uploads");
 String uploadPath = uploadDir.toFile().getAbsolutePath();
 
 registry.addResourceHandler("/uploads/**")
         .addResourceLocations("file:" + uploadPath + "/");
 */
  
 
}