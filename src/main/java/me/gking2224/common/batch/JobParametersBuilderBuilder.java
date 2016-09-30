package me.gking2224.common.batch;

import static me.gking2224.common.batch.BatchConstants.BATCH_DATE;
import static me.gking2224.common.batch.BatchConstants.EXECUTION_TIME;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class JobParametersBuilderBuilder {

    private boolean executionDate;
    
    @Autowired(required=true) @Qualifier("shortDateTimeFormat")
    private DateTimeFormatter shortDateTimeFormatter;

    @Autowired(required=true) @Qualifier("filenameDateFormat")
    private DateTimeFormatter filenameDateFormatter;

    private boolean batchDate;
    
    public JobParametersBuilderBuilder() {
    }
    
    public JobParametersBuilderBuilder addExecutionDate() {
        this.executionDate = true;
        return this;
    }
    
    public JobParametersBuilderBuilder addBatchDate() {
        this.batchDate = true;
        return this;
    }
    
    public JobParametersBuilder getJobParametersBuilder() {
        LocalDateTime now = LocalDateTime.now();
        JobParametersBuilder builder = new JobParametersBuilder();
        if (executionDate) builder.addString(EXECUTION_TIME, shortDateTimeFormatter.format(now));
        if (batchDate) builder.addString(BATCH_DATE, filenameDateFormatter.format(now));
        return builder;
        
    }
}