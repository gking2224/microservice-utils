package me.gking2224.common.batch.generic;

import static org.apache.commons.lang.exception.ExceptionUtils.getRootCauseMessage;

import java.util.List;
import java.util.Properties;
import java.util.function.Function;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.LineCallbackHandler;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertyResolver;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;

public abstract class AbstractBatchEtlStepBuilder<T, I, O> extends AbstractBatchStepBuilder<T>
implements ItemReadListener<I>, ItemWriteListener<O>, RetryListener, ItemProcessListener<I, O>, LineCallbackHandler, SkipListener<I, O>, ChunkListener {

    private int chunkSize = 5;
    private boolean allowStartIfComplete = true;
    private Function<StepExecutionHolder, List<Object>> skippedItemsProvider;
    @SuppressWarnings("unchecked")
    private Function<I, O> processorFunction = item -> (O)item;
    
    public AbstractBatchEtlStepBuilder(
            final StepBuilderFactory steps,
            final ConfigurableEnvironment environment,
            final Properties parentProperties,
            final String jobName,
            final String stepName
    ) {
        super(steps, environment, parentProperties, jobName, stepName);
    }
    
    public AbstractBatchEtlStepBuilder(StepBuilderFactory steps, PropertyResolver properties, String flowName,
            String stepName) {
        super(steps, properties, flowName, stepName);
    }

    public Step build() {
        
        return etlStepBuilder()
                .build();
    }

    @SuppressWarnings("unchecked")
    public T processorFunction(final Function<I, O> processorFunction) {
        this.processorFunction = processorFunction;
        return (T) this;
    }
    
    @SuppressWarnings("unchecked")
    public T chunkSize(final int chunkSize) {
        this.chunkSize = chunkSize;
        return (T) this;
    }
    
    @SuppressWarnings("unchecked")
    public T allowStartIfComplete(final boolean allowStartIfComplete) {
        this.allowStartIfComplete = allowStartIfComplete;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T skippedItems(Function<StepExecutionHolder, List<Object>> function) {
        this.skippedItemsProvider = function;
        return (T)this;
    }

    @SuppressWarnings("unchecked")
    protected SimpleStepBuilder<I, O> etlStepBuilder() {
        
        SimpleStepBuilder<I, O> builder = stepBuilder()
                .<I, O> chunk(this.chunkSize)
                .processor(i -> processorFunction.apply(i))
                .writer(getWriter())
                .reader(getReader());
        
        return (SimpleStepBuilder<I, O>)getFaultToleranceConfigurer()
                .configure(builder)
                .listener((ItemProcessListener<I,O>)this)
                .listener((RetryListener)this)
                .listener((SkipListener<I, O>)this)
                .listener((ChunkListener)this)
                .allowStartIfComplete(this.allowStartIfComplete);
    }
    
    protected abstract ItemReader<I> getReader();
    
    protected abstract ItemWriter<O> getWriter();

    protected String getReaderName() {
        return getStepName()+".reader";
    }

    protected String getWriterName() {
        return getStepName()+".writer";
    }

    @Override
    public final void onSkipInWrite(O item, Throwable t) {
        getLogger().error("{}: onSkipInWrite ({}): {}", getFullName(), item, getRootCauseMessage(t));
        skippedItemsProvider.apply(getStepExecutionHolder()).add(item);
        doOnSkipInWrite(item, t);
    }
    protected void doOnSkipInWrite(O item, Throwable t) {}

    @Override
    public final void onSkipInRead(Throwable t) {
        getLogger().error("{}: onSkipInRead: {}", getFullName(), getRootCauseMessage(t));
        skippedItemsProvider.apply(getStepExecutionHolder()).add(t);
        doOnSkipInRead(t);
    }
    protected void doOnSkipInRead(Throwable t) {}

    @Override
    public final void onSkipInProcess(I item, Throwable t) {
        getLogger().debug("{}: onSkipInProcess ({}): {}", getFullName(), item, getRootCauseMessage(t));
        skippedItemsProvider.apply(getStepExecutionHolder()).add(t);
        doOnSkipInProcess(item, t);
    }
    protected void doOnSkipInProcess(I item, Throwable t) {}

    @Override
    public final void afterChunkError(ChunkContext context) {
        getLogger().debug("{}: afterChunkError {}", getFullName(), context);
    }
    protected void doAfterChunkError(ChunkContext context) {}
    
    // WRITE LISTENER
    @Override
    public final void beforeWrite(List<? extends O> items) {
        getLogger().debug("{}: beforeWrite: {}", getFullName(), items);
    }
    protected void doBeforeWrite(List<? extends O> items) {}
    @Override
    public final void afterWrite(List<? extends O> items) {
        getLogger().debug("{}: afterWrite: {}", getFullName(), items);
    }
    protected void doAfterWrite(List<? extends O> items) {}
    @Override
    public final void onWriteError(Exception exception, List<? extends O> items) {
        getLogger().error("{}: onWriteError ({}): {}", getFullName(), items, getRootCauseMessage(exception));
    }
    protected void doOnWriteError(Exception exception, List<? extends O> items) {}
    @Override
    public final void beforeRead() {
        getLogger().debug("{}: beforeRead", getFullName());
    }
    
    // READ LISTENER
    protected void doBeforeRead() {}
    @Override
    public final void afterRead(I item) {
        getLogger().debug("{}: afterRead {}", getFullName(), item);
    }
    protected void doAfterRead(I item) {}
    @Override
    public final void onReadError(Exception ex) {
        getLogger().error("{}: onReadError: {}", getFullName(), getRootCauseMessage(ex));
    }
    protected void doOnReadError(Exception ex) {}
    
    // RETRY
    @Override
    public final <R, E extends Throwable> boolean open(RetryContext context, RetryCallback<R, E> callback) {
        getLogger().debug("{}: retry - open {}, {}", getFullName(), context, callback);
        return doOpen(context, callback);
    }
    protected <R, E extends Throwable> boolean doOpen(RetryContext context, RetryCallback<R, E> callback) {
        return true;
    }

    @Override
    public final <R, E extends Throwable> void close(RetryContext context, RetryCallback<R, E> callback,
            Throwable throwable) {
        getLogger().debug("{}: retry - close {}, {}: {}", getFullName(), context, callback, getRootCauseMessage(throwable));
        doClose(context, callback, throwable);
    }
    protected <R, E extends Throwable> void doClose(RetryContext context, RetryCallback<R, E> callback,
            Throwable throwable) {}
    
    @Override
    public <R, E extends Throwable> void onError(RetryContext context, RetryCallback<R, E> callback,
            Throwable throwable) {
        getLogger().error("{}: on retry error: {}", getFullName(), getRootCauseMessage(throwable));
        doOnError(context, callback, throwable);
    }
    protected <R, E extends Throwable> void doOnError(RetryContext context, RetryCallback<R, E> callback,
            Throwable throwable) {}

    // LINE CALLBACK HANDLER
    @Override
    public void handleLine(String line) {
        getLogger().debug("{}: skipping header line: {}", getFullName(), line);
        doHandleLine(line);
    }
    protected void doHandleLine(String line) {}
    
    // CHUNK
    @Override
    public final void beforeChunk(ChunkContext context) {
        getLogger().debug("{}: before chunk: {}", getFullName(), context);
        doBeforeChunk(context);
    }
    protected void doBeforeChunk(ChunkContext context) {}

    @Override
    public final void afterChunk(ChunkContext context) {
        getLogger().debug("{}: after chunk: {}", getFullName(), context);
        doAfterChunk(context);
    }
    protected void doAfterChunk(ChunkContext context) {}

    
    // PROCESS LISTENER
    
    @Override
    public void beforeProcess(I item) {
        getLogger().debug("{}: before process: {}", getFullName(), item);
    }

    @Override
    public void afterProcess(I item, O result) {
        getLogger().debug("{}: after process: {} -> {}", getFullName(), item, result);
    }

    @Override
    public void onProcessError(I item, Exception e) {
        getLogger().debug("{}: on process error ({}): {}", getFullName(), item, getRootCauseMessage(e));
    }
    
    // STEP EXECUTION
    protected final void doBeforeStep(StepExecution stepExecution) {
        if (skippedItemsProvider != null)
            skippedItemsProvider.apply(getStepExecutionHolder()).clear();
        doBeforeEtlStep(stepExecution);
    }

    protected void doBeforeEtlStep(StepExecution stepExecution) {}
}
