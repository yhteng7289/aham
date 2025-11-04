package com.pivot.aham.common.core.support.collection;

import com.google.common.base.Supplier;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

import java.util.Collection;


public final class MultiTableFactory {
    private MultiTableFactory() {
    }

    public static <R, C, V> IMultiTable<R, C, V> hashSetMultiTable() {
        return new MultiTableAdapter<>(HashBasedTable.<R, C, Collection<V>>create(), new HashSetSupplier<V>());
    }

    public static <R, C, V> IMultiTable<R, C, V> arrayListMultiTable() {
        return new MultiTableAdapter<R, C, V>(HashBasedTable.<R, C, Collection<V>>create(),
                new ArrayListSupplier<V>());
    }

    public static <R, C, V> IMultiTable<R, C, V> customMultiTable(Supplier<Table<R, C, Collection<V>>> tableSupplier,
                                                                     Supplier<Collection<V>> valueCollectionSupplier) {
        return new MultiTableAdapter<>(tableSupplier.get(), valueCollectionSupplier);
    }


    private static class HashSetSupplier<V> implements Supplier<Collection<V>> {
        @Override
        public Collection<V> get() {
            return Sets.newHashSet();
        }
    }

    private static class ArrayListSupplier<V> implements Supplier<Collection<V>> {
        @Override
        public Collection<V> get() {
            return Lists.newArrayList();
        }
    }
}
