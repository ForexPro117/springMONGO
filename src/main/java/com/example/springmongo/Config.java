package com.example.springmongo;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan({"com.example", "com.pirsbim"})
@ComponentScan({"com.example"})
@ConfigurationPropertiesScan("com.example")
@EnableJpaRepositories(basePackages = "com.example")
public class Config {

}
