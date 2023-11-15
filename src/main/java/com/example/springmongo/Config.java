package com.example.springmongo;

import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.UUID;

@Configuration
@EntityScan({"com.example", "com.pirsbim"})
@ComponentScan({"com.example"})
@EnableMongoRepositories(basePackages = "com.example")
@ConfigurationPropertiesScan("com.example")
@EnableJpaRepositories(basePackages = "com.example")
public class Config {

    @Bean
    public BeforeConvertCallback<EntityUuid> beforeSaveCallback() {

        return (entity, collection) -> {

            if (entity.getUuid() == null) {
                entity.setUuid(UUID.randomUUID());
            }
            return entity;
        };
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }


}
