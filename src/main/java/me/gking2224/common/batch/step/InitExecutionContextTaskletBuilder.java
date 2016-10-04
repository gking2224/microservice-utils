package me.gking2224.common.batch.step;

import static me.gking2224.common.batch.BatchConstants.BAD_SUFFIX;
import static me.gking2224.common.batch.BatchConstants.PROCESSED_SUFFIX;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

public class InitExecutionContextTaskletBuilder {

    private Map<String, Object> params = new HashMap<String, Object>();
    private ExecutionContext context;
    private Supplier<ExecutionContext> contextSupplier;
    
    public InitExecutionContextTaskletBuilder() {
        this.params.putAll(getDefaultParams());
    }
    
    public InitExecutionContextTaskletBuilder context(ExecutionContext context) {
        this.context = context;
        return this;
    }
    
    public InitExecutionContextTaskletBuilder contextSupplier(Supplier<ExecutionContext> contextSupplier) {
        this.contextSupplier = contextSupplier;
        return this;
    }
    
    public InitExecutionContextTaskletBuilder params(final Map<String, Object> params) {
        this.params.putAll(params);
        return this;
    }
    
    public InitExecutionContextTaskletBuilder put(final String key, final Object value) {
        this.params.put(key,  value);
        return this;
    }
    
    protected Map<String, Object> getDefaultParams() {
        
        Map<String, Object> x = new HashMap<String, Object>();
        x.put(BAD_SUFFIX, "bad");
        x.put(PROCESSED_SUFFIX, "processed");
        return x;
    }
    
    public Tasklet build() {
        
        return new Tasklet() {

            @SuppressWarnings("unchecked")
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                
                if (context == null && contextSupplier != null) {
                    context = contextSupplier.get();
                }
                params.forEach( (k, v) -> {
                    Object value = v;
                    if (isFunctionOfContext(v)) {
                        value = ((Function<ExecutionContext,? extends Object>)v).apply(context);
                    }
                    context.put(k, value);   
                });
                return RepeatStatus.FINISHED;
            }

        };
    }

    private boolean isFunctionOfContext(Object v) {
        
        boolean rv = true;
        
        rv = rv && Function.class.isAssignableFrom(v.getClass());
        
        return rv;
    }
}
