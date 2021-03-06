package com.github.rkmk.mapper;

import org.skife.jdbi.v2.ResultSetMapperFactory;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CustomMapperFactory implements ResultSetMapperFactory {

    private List<Class<?>> excludedTypes = new ArrayList<>();
    private List<FieldMapperFactory> overriddenFactories = new ArrayList<>();
    private ConcurrentHashMap<String, CustomMapper> cache = new ConcurrentHashMap<>();

    public CustomMapperFactory() {
        excludedTypes.add(Boolean.class);
        excludedTypes.add(Byte.class);
        excludedTypes.add(Short.class);
        excludedTypes.add(Integer.class);
        excludedTypes.add(Long.class);
        excludedTypes.add(Float.class);
        excludedTypes.add(Double.class);
        excludedTypes.add(BigDecimal.class);
        excludedTypes.add(String.class);
    }

    public CustomMapperFactory(Class<?>... excludedTypes) {
        this();
        for (Class<?> excludedType : excludedTypes) {
            this.excludedTypes.add(excludedType);
        }
    }

    @Override
    public boolean accepts(Class type, StatementContext ctx) {
        return !excludedTypes.contains(type);
    }

    @Override
    public ResultSetMapper mapperFor(Class type, StatementContext ctx) {
        return mapperFor(type, "");
    }

    public <M> CustomMapper<M> mapperFor(Class<M> type, String nameSpace) {
        String key = type.toString() + nameSpace;
        if( cache.contains(key) ) {
            return cache.get(key);
        }
        CustomMapper<M> mapper = new CustomMapper<>(type, overriddenFactories);
        cache.put(key, mapper);
        return mapper;
    }

    public void register(FieldMapperFactory factory) {
        overriddenFactories.add(factory);
    }
}
