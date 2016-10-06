package me.gking2224.common.batch.generic;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.FatalStepExecutionException;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;

import me.gking2224.common.batch.step.ListItemReader;

/**
 * 
 * @author gk
 *
 * @param <O>
 */
public class WriteRecordsStepBuilder<I, O> extends AbstractBatchEtlStepBuilder<WriteRecordsStepBuilder<I, O>, I, O> {
    
    private static Logger logger = LoggerFactory.getLogger(ProcessFileStepBuilder.class);

    private FieldExtractor<O> fieldExtractor;
    private Function<StepExecutionHolder, File> fileProvider;
    private Function<StepExecutionHolder, List<I>> itemsProvider;

    public WriteRecordsStepBuilder(final StepBuilderFactory steps, final Properties parentProperties,
            final String flowName, final String stepName) {
        super(steps, parentProperties, flowName, stepName);
    }

    public WriteRecordsStepBuilder<I, O> fileProvider(final Function<StepExecutionHolder, File> fileProvider) {
        this.fileProvider = fileProvider;
        return this;
    }

    public WriteRecordsStepBuilder<I, O> fieldExtractor(final FieldExtractor<O> fieldExtractor) {
        this.fieldExtractor = fieldExtractor;
        return this;
    }

    protected ItemWriter<O> getWriter() {
        FlatFileItemWriter<O> delegate = new FlatFileItemWriter<O>();
        delegate.setName(getWriterName());

        LineAggregator<O> lineAggregator = getLineAggregator();
        delegate.setLineAggregator(lineAggregator);
        
        InvocationHandler handler = new ContextResolvingHandler(delegate)
                .intercept(
                        "open",
                        (Supplier<Boolean>)resourceSettingInterceptor(delegate));
        @SuppressWarnings("unchecked")
        ItemWriter<O> writer = (ItemWriter<O>)Proxy.newProxyInstance(
                delegate.getClass().getClassLoader(), new Class[] {ItemWriter.class, ItemStream.class}, handler);
        
        return writer;
    }

    private Supplier<Boolean> resourceSettingInterceptor(final FlatFileItemWriter<O> delegate) {
        return () -> {
            File file = fileProvider.apply(getStepExecutionHolder());
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    throw new FatalStepExecutionException(format("Could not create file %s", file.getAbsolutePath()), e);
                }
            }
            FileSystemResource resource = new FileSystemResource(file);
            delegate.setResource(resource);
            return Boolean.TRUE;
        };
    }

    private Supplier<Boolean> itemListSettingDelegate(final ListItemReader<I> delegate) {
        return () -> {
            List<I> skippedItems = itemsProvider.apply(getStepExecutionHolder());
            delegate.setItemList(skippedItems);
            return Boolean.TRUE;
        };
    }

    @Bean
    public LineAggregator<O> getLineAggregator() {

        DelimitedLineAggregator<O> dla = new DelimitedLineAggregator<O>();
        dla.setFieldExtractor(fieldExtractor);
        return dla;
    }

    protected ItemReader<I> getReader() {
        ListItemReader<I> delegate = new ListItemReader<I>(getReaderName());

        InvocationHandler handler = new ContextResolvingHandler(delegate)
                .intercept(
                        "open",
                        (Supplier<Boolean>)itemListSettingDelegate(delegate));
        @SuppressWarnings("unchecked")
        ItemReader<I> reader = (ItemReader<I>)Proxy.newProxyInstance(
                delegate.getClass().getClassLoader(), new Class[] {ItemReader.class, ItemStream.class}, handler);
        
        return reader;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    public WriteRecordsStepBuilder<I, O> itemsProvider(Function<StepExecutionHolder, List<I>> itemsProvider) {
        this.itemsProvider = itemsProvider;
        return this;
    }

}
