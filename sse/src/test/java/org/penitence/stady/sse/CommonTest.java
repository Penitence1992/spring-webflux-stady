package org.penitence.stady.sse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerException;
import org.junit.Test;

import java.io.IOException;

public class CommonTest {

    @Test
    public void test() throws IOException, DockerException, InterruptedException {
        System.setProperty("DOCKER_HOST", "unix:///var/run/docker.sock");

//        DockerClientConfig dockerClientConfig = DefaultDockerClientConfig
//                .createDefaultConfigBuilder()
//                .build();
//        DockerClient dockerClient = DockerClientBuilder.getInstance(dockerClientConfig).build();
//        dockerClient.loadImageCmd(new FileInputStream("/Users/penitence/BuildRepository/redis.tar")).exec();
//        System.out.println("load one success");
//        dockerClient.loadImageCmd(new FileInputStream("/Users/penitence/BuildRepository/i.tar")).exec();
//        System.out.println("load two success");

        DockerClient dockerClient = DefaultDockerClient
                .builder()
                .readTimeoutMillis(1000)
                .connectTimeoutMillis(2000)
                .connectionPoolSize(10)
                .uri("http://10.10.1.213:2376")
                .build();


        LogStream logStream = dockerClient.logs("d78c953da5f4",
                DockerClient.LogsParam.create("tail", "10")
                , DockerClient.LogsParam.create("follow", "true")
                , DockerClient.LogsParam.create("stdout", "1")
                , DockerClient.LogsParam.create("stderr", "1"));

        logStream.attach(System.out, System.err);

//                        .forEachRemaining(System.out::println);
//        dockerClient.load(new FileInputStream("/Users/penitence/BuildRepository/redis.tar"))
//                .forEach(System.out::println);
//        dockerClient.load(new FileInputStream("/Users/penitence/BuildRepository/i.tar"))
//                .forEach(System.out::println);
//
//        System.out.println("success");

        ObjectMapper objectMapper = new ObjectMapper();

        System.out.println(objectMapper.writeValueAsString(dockerClient.listServices()));
    }
}
