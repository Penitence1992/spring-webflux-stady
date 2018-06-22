package org.penitence.stady.reactivemq.runner;

import org.springframework.boot.CommandLineRunner;

public class Keeper implements CommandLineRunner {


    @Override
    public void run(String... args) throws Exception {
        System.in.read();
    }
}
