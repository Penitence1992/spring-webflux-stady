package org.penitence.stady.reactivepg.dao;


import io.reactiverse.pgclient.PgClient;
import io.reactiverse.pgclient.PgConnection;
import io.reactiverse.pgclient.PgPool;
import io.reactiverse.pgclient.PgPoolOptions;
import org.penitence.stady.reactivepg.properties.ReactivePgProperty;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;

@Component
public class ReactivePgDataSource {

    private PgPool pool;

    public ReactivePgDataSource(ReactivePgProperty property) {
        PgPoolOptions options = new PgPoolOptions()
                .setMaxSize(property.getMaxPoolSize())
                .setUser(property.getUsername())
                .setPassword(property.getPassword())
                .setHost(property.getHost())
                .setDatabase(property.getName())
                .setPort(property.getPort());
        pool = PgClient.pool(options);

    }

    public Mono<PgConnection> getConnection() {
        return Mono.create(sink -> pool.getConnection(conResult -> {
            if (conResult.succeeded()) {
                sink.success(conResult.result());
            } else {
                sink.error(conResult.cause());
            }
        }));
    }
}
