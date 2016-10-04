package me.gking2224.common.batch.generic;

import static org.springframework.batch.item.file.transform.DelimitedLineTokenizer.DELIMITER_COMMA;

import java.util.function.Function;

import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.validation.BindException;

public class LineMapperBuilder<T> {
    
    private Function<String[], T> mapFunction;
    private String[] fieldNames;

    public LineMapper<T> build() {
        
        DefaultLineMapper<T> lm = new DefaultLineMapper<T>();
        
        lm.setFieldSetMapper(getMapFunction());
        lm.setLineTokenizer(getTokenizer());
        return lm;
    }

    public LineMapperBuilder<T> mapFunction(Function<String[], T> mapFunction) {
        this.mapFunction = mapFunction;
        return this;
    }

    public LineMapperBuilder<T> fieldName(String[] fieldNames) {
        this.fieldNames = fieldNames;
        return this;
    }

    private LineTokenizer getTokenizer() {
        
        DelimitedLineTokenizer dlt = new DelimitedLineTokenizer(DELIMITER_COMMA);
        if (this.fieldNames != null)
            dlt.setNames(fieldNames);
        return dlt;
    }

    private FieldSetMapper<T> getMapFunction() {
        
        return new FieldSetMapper<T>() {
            @Override
            public T mapFieldSet(FieldSet fieldSet) throws BindException {
                return mapFunction.apply(fieldSet.getValues());
            }
        };
    }

}
