package org.penitence.stady.reactivemongo.dao;

import org.bson.Document;
import org.penitence.stady.reactivemongo.dao.base.BaseCustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Repository
public class MonitoryCustomRepository extends BaseCustomRepository {

    public final static String COLLECTION_NAME = "DockerMetric";

    public MonitoryCustomRepository(ReactiveMongoOperations operations) {
        super(operations, COLLECTION_NAME);
    }

    public Mono<Page<Map>> findByPage(int page, int size) {
        if (page < 0) {
            page = 0;
        } else if (size < 1) {
            size = 10;
        }

        Pageable pageable = PageRequest.of(page, size);

        return findMapPage(createCriteria(), pageable, new Document());
    }

    public Flux<Map> findList(int max, Date startTime, Date endTime, String type, String dockerId) {
        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, "timestamp");
        Criteria criteria = createCriteria();
        ProjectionOperation projectionOperation = Aggregation.project()
                .andExclude("taskId", "module", "taskName", "nodeId");
        criteria.and("timestamp").gte(startTime).lte(Optional.ofNullable(endTime).orElse(new Date()));
        if (!StringUtils.isEmpty(type)) {
            criteria.and("metricName").is(type);
        }
        if (!StringUtils.isEmpty(dockerId)) {
            criteria.and("dockerId").is(dockerId);
        }
        return executeAggregate(
                Aggregation.match(criteria),
                sortOperation,
                Aggregation.limit(max),
                projectionOperation
        );
    }

//    private ConditionalOperators.Switch createConvertToDataSwitch(){
//        String[] metricType = new String[] {""};
//
//        return ConditionalOperators.Switch.switchCases(
//                ConditionalOperators.Switch.CaseOperator.when(ComparisonOperators.Eq.valueOf("$metricName").equalToValue("cpu")).then("$cpu")
//                ConditionalOperators.Switch.CaseOperator.when(ComparisonOperators.Eq.valueOf("$metricName").equalToValue("cpu")).then("$cpu"),
//                ConditionalOperators.Switch.CaseOperator.when(ComparisonOperators.Eq.valueOf("$metricName").equalToValue("cpu")).then("$cpu"),
//                ConditionalOperators.Switch.CaseOperator.when(ComparisonOperators.Eq.valueOf("$metricName").equalToValue("cpu")).then("$cpu"),
//                ConditionalOperators.Switch.CaseOperator.when(ComparisonOperators.Eq.valueOf("$metricName").equalToValue("cpu")).then("$cpu")
//        ).defaultTo(new Document());
//    }
}
