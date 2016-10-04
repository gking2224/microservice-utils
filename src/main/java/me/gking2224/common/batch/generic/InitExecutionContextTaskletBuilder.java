package me.gking2224.common.batch.generic;

import static me.gking2224.common.batch.BatchConstants.BAD_SUFFIX;
import static me.gking2224.common.batch.BatchConstants.PROCESSED_SUFFIX;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class InitExecutionContextTaskletBuilder extends AbstractTaskletBuilder<InitExecutionContextTaskletBuilder> {

    private Map<String, Object> contextAttributes = new HashMap<String, Object>();
    
    public InitExecutionContextTaskletBuilder() {
        this.contextAttributes.putAll(getDefaultParams());
    }
    
    public InitExecutionContextTaskletBuilder contextAttributes(final Map<String, Object> contextAttributes) {
        this.contextAttributes.putAll(contextAttributes);
        return this;
    }
    
    public InitExecutionContextTaskletBuilder put(final String key, final Object value) {
        this.contextAttributes.put(key,  value);
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

            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                
                contextAttributes.forEach( (k, v) -> {
                    Object value = v;
                    if (isFunctionOfContext(v)) {
                        value = getValueFromContext(value);
                    }
                    getContextHolder().putInJobContext(k, value);   
                });
                return RepeatStatus.FINISHED;
            }

        };
    }

    @SuppressWarnings("unchecked")
    protected Object getValueFromContext(Object value) {
        return ((Function<StepExecutionHolder,? extends Object>)value).apply(getContextHolder());
    }

    private boolean isFunctionOfContext(Object v) {
        
        boolean rv = true;
        
        rv = rv && Function.class.isAssignableFrom(v.getClass());
        // TODO check generic types?
        return rv;
    }
}
