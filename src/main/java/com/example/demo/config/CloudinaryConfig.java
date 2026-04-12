package com.example.demo.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dmop4j1da");
        config.put("api_key", "442415873951395");
        config.put("api_secret", "3WWIIunOBjU5M7dbktVJuhoQ0fk");
        return new Cloudinary(config);
    }
}
