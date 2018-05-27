package org.penitence.stady.reactivepg.dao;


import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class ReactivePgBaseCustomRepository {

    private ReactivePgManager reactivePgManager;

    private final ReactiveResultTransformer MAP_TRANSFORMER = ((colNames, row) -> {
        Map<String, Object> map = new HashMap<>();
        colNames.forEach(col -> map.put(col, row.getValue(col)));
        return map;
    });

    public ReactivePgBaseCustomRepository(ReactivePgManager reactivePgManager) {
        this.reactivePgManager = reactivePgManager;
    }

    public Flux<Map> findAll(String sql){
        return reactivePgManager.execute(sql, MAP_TRANSFORMER, Map.class);
    }


}
