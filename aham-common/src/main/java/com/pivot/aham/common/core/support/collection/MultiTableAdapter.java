package com.pivot.aham.common.core.support.collection;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Supplier;
import com.google.common.collect.Table;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;


public class MultiTableAdapter<R, C, V> implements IMultiTable<R, C, V> {

    private final Table<R, C, Collection<V>> backTable;
    private Supplier<Collection<V>> valueCollectionSupplier;
    private int size;

    protected MultiTableAdapter(Table<R, C, Collection<V>> backTable,
                                Supplier<Collection<V>> valueCollectionSupplier) {
        this.backTable = backTable;
        this.valueCollectionSupplier = valueCollectionSupplier;
    }

    @Override
    public boolean contains(Object rowKey, Object columnKey) {
        return backTable.contains(rowKey, columnKey);
    }

    @Override
    public Collection<V> get(Object rowKey, Object columnKey) {
        return backTable.get(rowKey, columnKey);
    }

    @Override
    public boolean isEmpty() {
        return backTable.isEmpty();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(R rowKey, C columnKey, V value) {
        Collection<V> values = backTable.get(rowKey, columnKey);
        if (values == null) {
            backTable.put(rowKey, columnKey, values = valueCollectionSupplier.get());
        }
        values.add(value);
        ++size;
    }

    @Override
    public void putAll(R rowKey, C columnKey, Iterable<? extends V> values) {
        checkNotNull(rowKey);
        checkNotNull(columnKey);
        checkNotNull(values);
        for (V value : values) {
            put(rowKey, columnKey, value);
        }
    }

    @Override
    public void remove(Object rowKey, Object columnKey, Object value) {
        Collection<V> values = backTable.get(rowKey, columnKey);
        if (values == null) {
            return;
        }
        if (values.remove(value)) {
            --size;
        }
        if (values.isEmpty()) {
            backTable.remove(rowKey, columnKey);
        }
    }

    @Override
    public Collection<V> remove(Object rowKey, Object columnKey) {
        Collection<V> values = backTable.remove(rowKey, columnKey);
        if (values == null) {
            return Collections.emptyList();
        }
        size -= values.size();
        return values;
    }

    @Override
    public Map<R, Collection<V>> column(C columnKey) {
        return backTable.column(columnKey);
    }

    @Override
    public Collection<Collection<V>> values() {
        Collection<Collection<V>> values = backTable.values();
        return values;
    }

    @Override
    public Map<R, Map<C, Collection<V>>> rowMap() {
        return backTable.rowMap();
    }
}
