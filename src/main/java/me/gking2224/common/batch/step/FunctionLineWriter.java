package me.gking2224.common.batch.step;

import java.util.List;
import java.util.function.Function;

import org.springframework.batch.item.ItemWriter;

public class FunctionLineWriter<T> implements ItemWriter<T> {

    private Function<T, Void> func;
    
    public FunctionLineWriter(Function<T, Void> func) {
        this.func = func;
    }
    @Override
    public void write(List<? extends T> items) throws Exception {
        items.forEach((T i) -> func.apply(i));
    }

}
