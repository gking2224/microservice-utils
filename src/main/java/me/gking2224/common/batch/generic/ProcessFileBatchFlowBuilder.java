package me.gking2224.common.batch.generic;

import static java.lang.String.format;
import static me.gking2224.common.batch.BatchConstants.BAD_FILE;
import static me.gking2224.common.batch.BatchConstants.COMPLETED_WITH_SKIPS;
import static me.gking2224.common.batch.BatchConstants.CSV_FILE;
import static me.gking2224.common.batch.BatchConstants.CSV_FILE_PATTERN;
import static me.gking2224.common.batch.BatchConstants.IN_DIR;
import static me.gking2224.common.batch.BatchConstants.OUT_DIR;
import static org.springframework.batch.core.ExitStatus.FAILED;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.item.file.transform.FieldExtractor;

import me.gking2224.common.batch.BatchConstants;

public class ProcessFileBatchFlowBuilder<I, O> extends AbstractBatchFlowBuilder {
    
    private Logger logger = LoggerFactory.getLogger(ProcessFileBatchFlowBuilder.class);

    private Function<String[], I> fieldsMapper;
    private String[] fieldNames;
    private Function<O, Void> writeFunction;
    private FieldExtractor<I> fieldExtractor;
    private Function<O, I> writeToFileProcessorFunction;
    private Function<I, O> readFromFileProcessorFunction;

    public ProcessFileBatchFlowBuilder(final StepBuilderFactory steps, final Properties parentProperties,
            final String jobName) {
        super(steps, parentProperties, jobName);
    }

    public Flow build() {

        Step init = new InitJobStepBuilder(getSteps(), getProperties(), getFlowName(), "init").params(params()).build();
        
        Step checkExists = new CheckFileExistsStepBuilder(getSteps(), getProperties(), getFlowName(), "checkExists")
                .fileProvider(fileProvider(getFileKey()))
                .build();
        
        Step process = new ProcessFileStepBuilder<I, O>(getSteps(), getProperties(), "process", getFlowName())
                .lineMapper(new LineMapperBuilder<I>().mapFunction(fieldsMapper).fieldName(fieldNames).build())
                .writeFunction(writeFunction)
                .fileProvider(fileProvider(getFileKey()))
                .skippedItems(skippedItems())
                .processorFunction(readFromFileProcessorFunction)
                .build();
        
        Step writeBadRecords = new WriteRecordsStepBuilder<O, I>(getSteps(), getProperties(), "writeBadRecords", getFlowName())
                .itemsProvider(skippedItems())
                .fileProvider(fileProvider(getBadFileKey()))
                .fieldExtractor(fieldExtractor)
                .processorFunction(writeToFileProcessorFunction)
                .build();
        
        Step markProcessed = new MoveFileStepBuilder(getSteps(), getProperties(), "markProcessed", getFlowName())
                .fileProvider(fileProvider(getFileKey()))
                .build();
        JobExecutionDecider decider = flowDecider();
        
        Flow skippedFilesFlow = new FlowBuilder<Flow>(getFlowName()+"skippedFiles")
            .start(writeBadRecords)
            .next(markProcessed)
            .end();

        Flow flow = new FlowBuilder<Flow>(getFlowName())
                .start(init)
                .next(checkExists)
                .next(process)
                .next(decider)
                    .on(COMPLETED_WITH_SKIPS).to(skippedFilesFlow)
                .from(decider)
                    .on(BatchConstants.COMPLETED).to(markProcessed)
                .build();
        
        return flow;
    }

    //    @Bean("flowDecider")
    protected JobExecutionDecider flowDecider() { 
        return new JobExecutionDecider() {
            
            @Override
            public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
                logger.debug("flow decider: jobExecution={}; stepexecution={}", jobExecution, stepExecution);
                FlowExecutionStatus rv = null;
                ExitStatus exitStatus = stepExecution.getExitStatus();
                
                if (stepExecution.getWriteSkipCount() > 0) {
                    rv = new FlowExecutionStatus(COMPLETED_WITH_SKIPS);
                }
                else if (FAILED.equals(exitStatus)){
                    rv = FlowExecutionStatus.FAILED;
                }
                else {
                    rv = FlowExecutionStatus.COMPLETED;
                }
                logger.debug("decision: {}", rv);
                return rv;
            }
        };
    }

    @SuppressWarnings("unchecked")
    private Function<StepExecutionHolder, List<O>> skippedItems() {
        return (ctx) -> (List<O>) ctx.getFromJobContext(skippedItemsKey(), List.class).orElseThrow(notInitialized(skippedItemsKey()));
    }

    private String skippedItemsKey() {
        return getJobKey(BatchConstants.SKIPPED_ITEMS_LIST);
    }

    private Function<StepExecutionHolder, File> fileProvider(final String key) {
        return (ctx) -> ctx.getFromJobContext(key, File.class).orElseThrow(notInitialized(key));
    }

    private String getFileKey() {
        return getJobKey(CSV_FILE);
    }

    private String getBadFileKey() {
        return getJobKey(BAD_FILE);
    }

    private Map<String, Object> params() {
        Map<String, Object> params = new HashMap<String, Object>();
        String filesDir = String.format(BatchConstants.BATCH_FILES_LOCATION, getBatchFilesDir(), getFlowName());
        final String inDir = filesDir + File.separator + "in";
        final String outDir = filesDir + File.separator + "out";

        params.put(getJobKey(CSV_FILE_PATTERN), format("%s-{yyyymmdd}.csv", getFlowName()));
        params.put(getFileKey(), (Function<StepExecutionHolder, File>) (StepExecutionHolder ctx) -> new File(inDir,
                format("/%s-%s.csv", getFlowName(), ctx.getBatchDate())));
        params.put(getBadFileKey(), (Function<StepExecutionHolder, File>) (StepExecutionHolder ctx) -> new File(inDir,
                format("/%s-%s.csv.bad", getFlowName(), ctx.getBatchDate())));
        params.put(getJobKey(IN_DIR), inDir);
        params.put(getJobKey(OUT_DIR), outDir);
        params.put(skippedItemsKey(), new ArrayList<O>(0));
        return params;
    }

    public ProcessFileBatchFlowBuilder<I, O> fieldsMapper(final Function<String[], I> fieldsMapper) {
        this.fieldsMapper = fieldsMapper;
        return this;
    }

    public ProcessFileBatchFlowBuilder<I, O> fieldExtractor(final FieldExtractor<I> fieldExtractor) {
        this.fieldExtractor = fieldExtractor;
        return this;
    }

    public ProcessFileBatchFlowBuilder<I, O> fieldNames(String... fieldNames) {
        this.fieldNames = fieldNames;
        return this;
    }

    public ProcessFileBatchFlowBuilder<I, O> writeFunction(Function<O, Void> writeFunction) {
        this.writeFunction = writeFunction;
        return this;
    }

    public ProcessFileBatchFlowBuilder<I, O> readFromFileProcessorFunction(Function<I, O> readFromFileProcessorFunction) {
        this.readFromFileProcessorFunction = readFromFileProcessorFunction;
        return this;
    }

    public ProcessFileBatchFlowBuilder<I, O> writeToFileProcessorFunction(Function<O, I> writeToFileProcessorFunction) {
        this.writeToFileProcessorFunction = writeToFileProcessorFunction;
        return this;
    }

}
