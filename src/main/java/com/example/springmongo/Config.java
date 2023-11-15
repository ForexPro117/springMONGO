package com.example.springmongo;

import com.example.springmongo.converters.ByteToDoubleConverter;
import com.example.springmongo.converters.ByteToFloatConverter;
import com.example.springmongo.converters.ByteToIntConverter;
import com.example.springmongo.converters.DoubleToByteConverter;
import com.example.springmongo.converters.FloatToByteConverter;
import com.example.springmongo.converters.IntToByteConverter;
import java.util.ArrayList;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.core.convert.converter.Converter;

import java.util.UUID;

@Configuration
@EntityScan({"com.example", "com.pirsbim"})
@ComponentScan({"com.example"})
@EnableMongoRepositories(basePackages = "com.example")
@EnableAutoConfiguration
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
    public MongoCustomConversions customConversions()
    {
        List<Converter<?, ?>> converterList = new ArrayList<>();
        converterList.add(new IntToByteConverter());
        converterList.add(new ByteToIntConverter());
        converterList.add(new DoubleToByteConverter());
        converterList.add(new ByteToDoubleConverter());
        converterList.add(new FloatToByteConverter());
        converterList.add(new ByteToFloatConverter());
        return new MongoCustomConversions(converterList);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }


}
