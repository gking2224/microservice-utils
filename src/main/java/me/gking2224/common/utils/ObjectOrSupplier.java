package me.gking2224.common.utils;

import java.util.function.Supplier;

public class ObjectOrSupplier<T> {

    private T object;
    
    private Supplier<T> supplier;
    
    public ObjectOrSupplier(final T object) {
        this.object = object;
    }
    
    public ObjectOrSupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }
    
    public T get() {
        if (object == null && supplier != null) {
            object = supplier.get();
        }
        return object;
    }
}
