package me.gking2224.common.batch.generic;

import java.util.function.Supplier;

public abstract class AbstractTaskletBuilder<T extends AbstractTaskletBuilder<T>> {

    
    private Supplier<StepExecutionHolder> contextHolderSupplier;
    private StepExecutionHolder contextHolder;
    
    @SuppressWarnings("unchecked")
    public T contextSupplier(Supplier<StepExecutionHolder> contextHolderSupplier) {
        this.contextHolderSupplier = contextHolderSupplier;
        return (T)this;
    }
    
    @SuppressWarnings("unchecked")
    public T context(StepExecutionHolder contextHolder) {
        this.contextHolder = contextHolder;
        return (T)this;
    }
    
    protected StepExecutionHolder getContextHolder() {
        if (contextHolder != null) return contextHolder;
        else if (contextHolderSupplier != null) return contextHolderSupplier.get();
        else return null;
    }

    protected <B> B get(B object, Supplier<B> supplier) {
        if (object != null) return object;
        else if (supplier != null) return supplier.get();
        else return null;
    }
}
