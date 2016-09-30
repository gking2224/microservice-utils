package me.gking2224.common.batch.step;

import static me.gking2224.common.batch.BatchConstants.SKIPPED_ITEMS_LIST;
import static org.apache.commons.lang.exception.ExceptionUtils.getRootCauseMessage;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.annotation.AfterChunkError;
import org.springframework.batch.core.annotation.OnSkipInProcess;
import org.springframework.batch.core.annotation.OnSkipInRead;
import org.springframework.batch.core.annotation.OnSkipInWrite;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.FatalStepExecutionException;
import org.springframework.batch.item.file.LineCallbackHandler;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;

public abstract class AbstractEtlStep<I,O> extends AbstractStep
implements ItemReadListener<I>, ItemWriteListener<O>, RetryListener, LineCallbackHandler {

    @Override
    protected final void doBeforeStep() {
        putInJobContext(SKIPPED_ITEMS_LIST, new ArrayList<O>());
        doBeforeEtlStep();
    }
    protected void doBeforeEtlStep() {}
    
    @OnSkipInWrite
    public final void onSkipInWrite(O item, Throwable t) {
        getLogger().error("onSkipInWrite ({}): {}", item, getRootCauseMessage(t));
        @SuppressWarnings("unchecked")
        List<O> failures = getFromJobContext(SKIPPED_ITEMS_LIST, List.class).orElseThrow(
                () -> new FatalStepExecutionException("SKIPPED_ITEMS_LIST not initialized", null));
        failures.add(item);
        doOnSkipInWrite(item, t);
    }
    protected void doOnSkipInWrite(O item, Throwable t) {}

    @OnSkipInRead
    public final void onSkipInRead(Throwable t) {
        getLogger().debug("onSkipInRead: {}", getRootCauseMessage(t));
        doOnSkipInRead(t);
    }
    protected void doOnSkipInRead(Throwable t) {}

    @OnSkipInProcess
    public final void onSkipInProcess(I item, Throwable t) {
        getLogger().debug("onSkipInProcess ({}): {}", item, getRootCauseMessage(t));
        doOnSkipInProcess(item, t);
    }
    protected void doOnSkipInProcess(I item, Throwable t) {}

    @AfterChunkError
    public final void afterChunkError(ChunkContext context) {
        getLogger().error("afterChunkError {}", context);
    }
    protected void doAfterChunkError(ChunkContext context) {}
    
    // WRITE LISTENER
    @Override
    public final void beforeWrite(List<? extends O> items) {
        getLogger().debug("beforeWrite: {}", items);
    }
    protected void doBeforeWrite(List<? extends O> items) {}
    @Override
    public final void afterWrite(List<? extends O> items) {
        getLogger().debug("afterWrite: {}", items);
    }
    protected void doAfterWrite(List<? extends O> items) {}
    @Override
    public final void onWriteError(Exception exception, List<? extends O> items) {
        getLogger().error("onWriteError ({}): {}", items, getRootCauseMessage(exception));
    }
    protected void doOnWriteError(Exception exception, List<? extends O> items) {}
    @Override
    public final void beforeRead() {
        getLogger().debug("beforeRead");
    }
    
    // READ LISTENER
    protected void doBeforeRead() {}
    @Override
    public final void afterRead(I item) {
        getLogger().debug("afterRead {}", item);
    }
    protected void doAfterRead(I item) {}
    @Override
    public final void onReadError(Exception ex) {
        getLogger().error("onReadError: {}", getRootCauseMessage(ex));
    }
    protected void doOnReadError(Exception ex) {}
    
    // RETRY
    @Override
    public final <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
        getLogger().debug("retry - open {}, {}", context, callback);
        return doOpen(context, callback);
    }
    protected <T, E extends Throwable> boolean doOpen(RetryContext context, RetryCallback<T, E> callback) {
        return false;
    }

    @Override
    public final <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback,
            Throwable throwable) {
        getLogger().debug("retry - close {}, {}: {}", context, callback, getRootCauseMessage(throwable));
        doClose(context, callback, throwable);
    }
    protected <T, E extends Throwable> void doClose(RetryContext context, RetryCallback<T, E> callback,
            Throwable throwable) {}
    
    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,
            Throwable throwable) {
        getLogger().error("on retry error: {}", getRootCauseMessage(throwable));
        doOnError(context, callback, throwable);
    }
    protected <T, E extends Throwable> void doOnError(RetryContext context, RetryCallback<T, E> callback,
            Throwable throwable) {}

    // LINE CALLBACK HANDLER
    @Override
    public void handleLine(String line) {
        getLogger().debug("skipping header line: {}", line);
        doHandleLine(line);
    }
    protected void doHandleLine(String line) {}
    
}
