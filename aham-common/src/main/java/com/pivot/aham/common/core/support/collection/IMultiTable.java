package com.pivot.aham.common.core.support.collection;

import java.util.Collection;
import java.util.Map;


public interface IMultiTable<R, C, V> {

    boolean contains(Object rowKey, Object columnKey);

    Collection<V> get(Object rowKey, Object columnKey);

    boolean isEmpty();

    int size();

    void put(R rowKey, C columnKey, V value);

    void putAll(R rowKey, C columnKey, Iterable<? extends V> values);

    void remove(Object rowKey, Object columnKey, Object value);

    Collection<V> remove(Object rowKey, Object columnKey);

    Map<R, Collection<V>> column(C columnKey);

    Collection<Collection<V>> values();

    Map<R, Map<C, Collection<V>>> rowMap();
}
