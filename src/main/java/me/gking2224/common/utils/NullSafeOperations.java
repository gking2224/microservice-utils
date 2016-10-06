package me.gking2224.common.utils;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class NullSafeOperations {

    public static String toStringOrNull(final Object o) {
        return o == null ? null : o.toString();
    }
    
    public static <R, T> R doIfNotNull(final T o, final Function<T,R> operation) {
        return o == null ? null : operation.apply(o);
    }
    
    public static <T> void doIfNotNull(final T o, final Consumer<T> operation) {
        if (o != null) operation.accept(o);
    }
    
    public static <R> R getIfNotNull(final Object o, final Supplier<R> operation) {
        return (o == null) ? null : operation.get();
    }
}
