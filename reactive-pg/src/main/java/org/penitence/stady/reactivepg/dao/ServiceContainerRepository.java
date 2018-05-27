package org.penitence.stady.reactivepg.dao;

import io.reactiverse.pgclient.PgPool;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;


@org.springframework.stereotype.Repository
public class ServiceContainerRepository extends ReactivePgBaseCustomRepository{

    private PgPool pool;

    private final ReactiveResultTransformer MAP_TRANSFORMER = ((colNames, row) -> {
        Map<String, Object> map = new HashMap<>();
        colNames.forEach(col -> map.put(col, row.getValue(col)));
        return map;
    });

    public ServiceContainerRepository(ReactivePgManager reactivePgManager) {
        super(reactivePgManager);
    }

    public Flux<Map> findAll() {
        String sql = "SELECT * FROM service_container";
        return findAll(sql);
    }
}
