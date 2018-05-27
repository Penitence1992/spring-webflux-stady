package org.penitence.stady.reactivepg.dao;

import io.reactiverse.pgclient.PgPreparedQuery;
import io.reactiverse.pgclient.PgResult;
import io.reactiverse.pgclient.Row;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ReactivePgManager {

    private final ReactivePgDataSource dataSource;

    public ReactivePgManager(ReactivePgDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Mono<PgPreparedQuery> createNativeSql(String sql) {
        return Mono.create(sink -> dataSource.getConnection()
                .subscribe(pgConnection -> pgConnection.prepare(sql, preparedQuery -> {
                    if (preparedQuery.succeeded()) {
                        sink.success(preparedQuery.result());
                    } else {
                        sink.error(preparedQuery.cause());
                        sink.success();
                    }
                })));

    }

    public <T> Flux<T> execute(String sql, ReactiveResultTransformer transformer, Class<T> clazz) {

        return Flux.create(sink -> dataSource.getConnection()
                .subscribe(pgConnection -> pgConnection.prepare(sql, preparedQuery -> {
                    if (preparedQuery.succeeded()) {
                        preparedQuery.result()
                                .execute(pgResult -> {
                                    if (pgResult.succeeded()) {
                                        PgResult<Row> rowResult = pgResult.result();
                                        rowResult.forEach(row -> sink.next((T) transformer.transformTuple(rowResult.columnsNames(), row)));
                                        sink.complete();
                                    } else {
                                        sink.error(pgResult.cause());
                                        sink.complete();
                                    }
                                });
                    } else {
                        sink.error(preparedQuery.cause());
                        sink.complete();
                    }
                }).close()));
    }
}
