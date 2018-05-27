package org.penitence.stady.reactivemongo.dao.base;

import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@NoRepositoryBean
public class BaseCustomRepository {

    protected final ReactiveMongoOperations operations;

    private final String collectionName;

    public BaseCustomRepository(ReactiveMongoOperations operations, String collectionName) {
        this.operations = operations;
        this.collectionName = collectionName;
    }

    protected Mono<Map> findOneByQuery(String key, Object value) {
        return operations.findOne(new Query(Criteria.where(key).is(value)), Map.class, collectionName)
                .doOnNext(this::remoteId);
    }

    protected Mono<Map> saveOneMap(Map map) {
        return operations.save(map, collectionName);
    }

    protected void saveOrUpdate(Map map, String idKey, Object idValue) {
        operations.updateFirst(new Query(Criteria.where(idKey).is(idValue)), Update.fromDocument(new Document(map), "_id"), collectionName);
//        Map dbData = findOneByQuery(idKey, idValue);
    }

    protected Mono<Page<Map>> findMapPage(Criteria criteria, Pageable pageable, Document fieldObject) {
        Query query = new BasicQuery(new Document(), fieldObject);
        return CompletableFuture
                .supplyAsync(() -> operations.count(query, Map.class, collectionName).block())
                .thenApply(count -> operations.find(query.addCriteria(criteria).skip(pageable.getOffset()).limit(pageable.getPageSize()).with(pageable.getSort()), Map.class, collectionName)
                .doOnNext(this::remoteId)
                .collectList()
                .map(data -> (Page<Map>)new PageImpl<>(data, pageable, count)))
                .join();
//        ArrayBlockingQueue<Long> count = new ArrayBlockingQueue<>(255);
//        operations.count(query, Map.class, collectionName)
//                .subscribe(count::add);
//        AtomicLong along = new AtomicLong(0);
//        try {
//            along.set(count.take());
//        } catch (InterruptedException e) {
//        }
//        Flux<Map> data = operations.find(query.addCriteria(criteria).skip(pageable.getOffset()).limit(pageable.getPageSize()).with(pageable.getSort()), Map.class, collectionName);
//        return data
//                .doOnNext(this::remoteId)
//                .collectList()
//                .map(datas -> new PageImpl<>(datas, pageable, along.get()));
    }

    protected Flux<Map> executeAggregate(AggregationOperation... aggregationOperations){
        return operations.aggregate(Aggregation.newAggregation(aggregationOperations), collectionName, Map.class)
                .doOnNext(this::remoteId);
    }


    protected Flux<Map> findMapList(Criteria criteria, Document fieldObject) {
        Query query = new BasicQuery(new Document(), fieldObject);
        Flux<Map> data = operations.find(query.addCriteria(criteria), Map.class, collectionName);
        return data.doOnNext(this::remoteId);
    }

    protected Flux<Map> findMapList(Criteria criteria, Document fieldObject, Sort sort) {
        Assert.notNull(sort, "sort field can't be null");
        Query query = new BasicQuery(new Document(), fieldObject);
        return operations.find(query.addCriteria(criteria).with(sort), Map.class, collectionName)
                .doOnNext(this::remoteId);
    }

    protected Mono<Long> deleteOneMap(String idKey, Object idValue) {
        return operations.remove(new Query(Criteria.where(idKey).is(idValue)), Map.class, collectionName)
                .map(DeleteResult::getDeletedCount);
    }

    protected Mono<Long> findCount(Criteria criteria) {
        return operations.count(new Query(criteria), collectionName);
    }

    protected Criteria createCriteria() {
        return Criteria.where("_id").exists(true);
    }

    private void remoteId(Map data){
        data.remove("_id");
    }


}
