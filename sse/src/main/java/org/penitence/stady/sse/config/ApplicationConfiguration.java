package org.penitence.stady.sse.config;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    private final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfiguration.class);


    @Bean
    protected DockerClient dockerClient(@Value("${docker.host:http://10.10.1.213:2376}") String managerHost){
        LOGGER.info("User client type service service host : {}", managerHost);
        return DefaultDockerClient
                .builder()
                .readTimeoutMillis(5000)
                .connectTimeoutMillis(10000)
                .connectionPoolSize(10)
                .uri(managerHost)
                .build();
    }

}
