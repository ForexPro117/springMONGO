package com.example.springmongo;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan({"com.example", "com.pirsbim"})
@ComponentScan({"com.example"})
@ConfigurationPropertiesScan("com.example")
@EnableJpaRepositories(basePackages = "com.example")
@EnableCassandraRepositories(basePackages = "com.example")
@EnableAutoConfiguration
public class Config {

}
