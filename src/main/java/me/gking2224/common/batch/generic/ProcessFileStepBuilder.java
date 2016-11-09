package me.gking2224.common.batch.generic;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineCallbackHandler;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.io.FileSystemResource;

import me.gking2224.common.batch.step.FlatFileItemReaderBuilder;
import me.gking2224.common.batch.step.FunctionLineWriter;

public class ProcessFileStepBuilder<I, O>
extends AbstractBatchEtlStepBuilder<ProcessFileStepBuilder<I, O>, I, O> {
    
    private static Logger logger = LoggerFactory.getLogger(ProcessFileStepBuilder.class);
    
    protected Logger getLogger() { return logger; }
    
    public ProcessFileStepBuilder(
            final StepBuilderFactory steps,
            final ConfigurableEnvironment environment,
            final Properties parentProperties,
            final String flowName,
            final String stepName
    ) {
        super(steps, environment, parentProperties, flowName, stepName);
    }

    public ProcessFileStepBuilder(StepBuilderFactory steps, PropertyResolver properties, String flowName, String stepName) {
        super(steps, properties, flowName, stepName);
    }

    private Function<O, Void> writeFunction;
    private LineMapper<I> lineMapper;
    private Function<StepExecutionHolder, File> fileProvider;
    
    public ProcessFileStepBuilder<I,O> lineMapper(LineMapper<I> lineMapper) {
        this.lineMapper = lineMapper;
        return this;
    }
    
    public ProcessFileStepBuilder<I,O> writeFunction(Function<O, Void> writeFunction) {
        this.writeFunction = writeFunction;
        return this;
    }

    public ProcessFileStepBuilder<I,O> fileProvider(final Function<StepExecutionHolder, File> fileProvider) {
        this.fileProvider = fileProvider;
        return this;
    }
    
    protected ItemReader<I> getReader() {
        
        final FlatFileItemReader<I> delegate = new FlatFileItemReaderBuilder<I>(getProperties())
                .name(getReaderName())
                .lineMapper(this.lineMapper)
                .skippedLinesCallback((LineCallbackHandler)this)
                .build();
        
        InvocationHandler handler = new ContextResolvingHandler(delegate)
                .intercept(
                        "open",
                        (Supplier<Boolean>)resourceSettingInterceptor(delegate));
        @SuppressWarnings("unchecked")
        ItemReader<I> reader = (ItemReader<I>)Proxy.newProxyInstance(
                delegate.getClass().getClassLoader(), new Class[] {ItemReader.class, ItemStream.class}, handler);
        return reader;
    }

    private Supplier<Boolean> resourceSettingInterceptor(final FlatFileItemReader<I> delegate) {
        return () -> {
            File file = fileProvider.apply(getStepExecutionHolder());
            FileSystemResource resource = new FileSystemResource(file);
            delegate.setResource(resource);
            return Boolean.TRUE;
        };
    }
    
    protected FunctionLineWriter<O> getWriter() {
        return new FunctionLineWriter<O>(this.writeFunction);
    }
}
