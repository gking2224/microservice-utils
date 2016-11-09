package me.gking2224.common.batch.generic;

import static java.lang.String.format;

import java.util.Properties;
import java.util.function.Function;

import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.core.env.ConfigurableEnvironment;

import me.gking2224.common.batch.BatchUtils;

public abstract class AbstractEtlBatchConfiguration<L, E> extends AbstractBatchConfiguration {

    
    private static final String NULL = "<null>";
    
    protected final ProcessFileBatchFlowBuilder<L, E> fileProcessFlowBuilder(
            final StepBuilderFactory steps,
            final ConfigurableEnvironment environment,
            final Properties batchProperties
    ) {
        return new ProcessFileBatchFlowBuilder<L, E>(
                steps, environment, batchProperties, getFlowName())
            .writeFunction(writeFunction())
            .fieldNames(fieldNames())
            .fieldsMapper(fromFields())
            .readFromFileProcessorFunction(toEntityObject())
            .writeToBadFileProcessorFunction(toBadFileEntry());
    }

    @SuppressWarnings("unchecked")
    protected final Function<Object, String> toBadFileEntry() {
        return (o) -> {
            if (o == null) return NULL;
            else if (getOutClass().isAssignableFrom(o.getClass())) {
                return convertToBadFileLine((E)o);
            }
            else if (getInClass().isAssignableFrom(o.getClass())) {
                return convertToBadFileLine((L)o);
            }
            else if (Throwable.class.isAssignableFrom(o.getClass())) {
                
                return BatchUtils.exceptionToFileString((Throwable)o);
            }
            else if (String.class.isAssignableFrom(o.getClass())) return (String)o;
            else return format("Error processing %s", o);
        };
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected String convertToBadFileLine(final Object object) {
        Object obj = object;
        if (getOutClass().isAssignableFrom(obj.getClass())) {
            obj = toRowObject().apply((E)object);
        }
        if (obj instanceof FileRowEntry) {
            return String.join(",", ((FileRowEntry)obj).getTokens());
        }
        else return format("Error processing %s", obj);
    }

    protected abstract Function<String[], L> fromFields();
    protected abstract Function<E, L> toRowObject();
    protected abstract Function<L, E> toEntityObject();
    protected abstract Class<E> getOutClass();
    protected abstract Class<L> getInClass();
    protected abstract String[] fieldNames();
    protected abstract Function<E, Void> writeFunction();
}
