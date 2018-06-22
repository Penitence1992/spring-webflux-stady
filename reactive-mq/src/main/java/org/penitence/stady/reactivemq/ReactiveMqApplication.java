package org.penitence.stady.reactivemq;

import org.penitence.stady.reactivemq.runner.Keeper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ReactiveMqApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveMqApplication.class, args);
    }

    @Bean
    public Keeper mqMessageReceiver(){
        return new Keeper();
    }
}
