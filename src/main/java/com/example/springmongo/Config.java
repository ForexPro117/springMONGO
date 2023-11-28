package com.example.springmongo;

import com.example.springmongo.geometrydata.EntityUuid;
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

//    @Bean
//    public SmileMapper LZ4SmileMapper() {
//        return (SmileMapper) SmileMapper.builder(new SmileFactoryBuilder(new SmileFactory())
//                        .inputDecorator(new InputDecorator() {
//                            @Override
//                            public InputStream decorate(IOContext context, InputStream inputStream) {
//                                return new LZ4BlockInputStream(inputStream);
//                            }
//
//                            @Override
//                            public InputStream decorate(IOContext context, byte[] bytes, int offset, int length) {
//                                return new LZ4BlockInputStream(new ByteArrayInputStream(bytes, offset, length));
//                            }
//
//                            @Override
//                            public Reader decorate(IOContext context, Reader reader) {
//                                return new InputStreamReader(new LZ4BlockInputStream(new ReaderInputStream(reader)), UTF_8);
//                            }
//                        })
//                        .outputDecorator(new OutputDecorator() {
//                            @Override
//                            public OutputStream decorate(IOContext context, OutputStream outputStream) {
//                                return new LZ4BlockOutputStream(outputStream,
//                                        32 * 1024, LZ4Factory.fastestInstance().fastCompressor());
//                            }
//
//                            @Override
//                            public Writer decorate(IOContext context, Writer writer) {
//                                return new OutputStreamWriter(new LZ4BlockOutputStream(new WriterOutputStream(writer, UTF_8))
//                                );
//                            }
//                        }).build()).build().registerModule(PARAMETER_NAMES_MODULE)
//                .registerModule(JAVA_TIME_MODULE)
//                .registerModule(JDK_8_MODULE);
//    }

}
