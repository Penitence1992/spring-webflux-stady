package org.penitence.stady.reactivepg.dao;

import io.reactiverse.pgclient.Row;

import java.util.List;

public interface ReactiveResultTransformer {

    public Object transformTuple(List<String> colNames, Row row);
}
