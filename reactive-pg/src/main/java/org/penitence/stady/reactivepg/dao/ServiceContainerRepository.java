package org.penitence.stady.reactivepg.dao;

import reactor.core.publisher.Flux;

import java.util.Map;


@org.springframework.stereotype.Repository
public class ServiceContainerRepository extends ReactivePgBaseCustomRepository{

    public ServiceContainerRepository(ReactivePgManager reactivePgManager) {
        super(reactivePgManager);
    }

    public Flux<Map> findAll() {
        String sql = "SELECT * FROM service_container";
        return findAll(sql);
    }
}
