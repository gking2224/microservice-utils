package me.gking2224.common.batch.step;

import static me.gking2224.common.batch.BatchConstants.BAD_SUFFIX;
import static me.gking2224.common.batch.BatchConstants.PROCESSED_SUFFIX;
import static org.springframework.batch.repeat.RepeatStatus.FINISHED;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

public class SetParamsTaskletBuilder {

    private Map<String, Object> params = new HashMap<String, Object>();
    private ExecutionContext context;
    
    public SetParamsTaskletBuilder() {
        this.params.putAll(getDefaultParams());
    }
    
    public SetParamsTaskletBuilder context(ExecutionContext context) {
        this.context = context;
        return this;
    }
    
    public SetParamsTaskletBuilder params(final Map<String, Object> params) {
        this.params.putAll(params);
        return this;
    }
    
    public SetParamsTaskletBuilder put(final String key, final Object value) {
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

            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                params.forEach( (k, v) -> context.put(k, v) );
                return FINISHED;
            }
        };
    }

}
