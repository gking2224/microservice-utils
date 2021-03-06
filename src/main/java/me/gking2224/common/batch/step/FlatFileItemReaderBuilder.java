package me.gking2224.common.batch.step;

import static java.lang.String.format;
import static me.gking2224.common.utils.PropertyResolverUtils.getBoolean;
import static me.gking2224.common.utils.PropertyResolverUtils.getInteger;
import static me.gking2224.common.utils.PropertyResolverUtils.getStringArray;

import java.io.File;

import org.springframework.batch.core.step.FatalStepExecutionException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineCallbackHandler;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.separator.RecordSeparatorPolicy;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.io.FileSystemResource;

import me.gking2224.common.batch.generic.SkipBlanksRecordSeparatorPolicy;

public class FlatFileItemReaderBuilder<T> {

    private static final int DEFAULT_HEADER_LINES = 1;

    private static final boolean DEFAULT_STRICT = false;
    
    private PropertyResolver properties;
    private File file;
    private String name;
    private LineCallbackHandler handler;

    private LineMapper<T> lineMapper;

    private RecordSeparatorPolicy recordSeparatorPolicy = new SkipBlanksRecordSeparatorPolicy();

    public FlatFileItemReaderBuilder(PropertyResolver properties) {
        this.properties = properties;
    }

    public FlatFileItemReaderBuilder<T> file(File file) {
        this.file = file;
        return this;
    }

    public FlatFileItemReaderBuilder<T> name(String name) {
        this.name = name;
        return this;
    }

    public FlatFileItemReaderBuilder<T> skippedLinesCallback(LineCallbackHandler handler) {
        this.handler = handler;
        return this;
    }

    public FlatFileItemReader<T> build() {
        
        FlatFileItemReader<T> fr = new FlatFileItemReader<T>();
        if (file != null) {
            if (!file.exists())
                throw new FatalStepExecutionException(format("File (%s) not found for reading", file.getAbsolutePath()), null);
            FileSystemResource resource = new FileSystemResource(file);
            fr.setResource(resource);
        }

        fr.setName(name);
        fr.setSkippedLinesCallback(handler);
        fr.setLineMapper(lineMapper);
        fr.setLinesToSkip(getInteger(properties, "reader.headerLines", DEFAULT_HEADER_LINES));
        fr.setStrict(getBoolean(properties, "reader.strict", DEFAULT_STRICT));
        fr.setComments(getStringArray(properties, "reader.comments", new String[] {"#"}));
        fr.setRecordSeparatorPolicy(recordSeparatorPolicy);
        
        return fr;
    }

    public FlatFileItemReaderBuilder<T> lineMapper(LineMapper<T> lineMapper) {
        this.lineMapper = lineMapper;
        return this;
    }

}
