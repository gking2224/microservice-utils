package me.gking2224.common.batch;

import static java.lang.String.format;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.gking2224.common.utils.ExceptionUtils;

@Component
public class JobRunner {

    @Autowired JobRegistry jobRegistry;
    @Autowired JobParametersBuilderBuilder paramBuilder;
    @Autowired JobLauncher jobLauncher;

    public JobRunResult runJob(String jobName) {
        JobRunResult result = new JobRunResult();
        try {
            Job mainBatchJob = jobRegistry.getJob(jobName);
            
            JobParametersBuilder jobParametersBuilder = paramBuilder.getJobParametersBuilder();
            
            JobExecution execution = jobLauncher.run(mainBatchJob, jobParametersBuilder.toJobParameters());

            result.setExecutionResult(execution);
            result.setStatusMessage(
                    format("Job %s executed successfuly with executionId=%d and instanceId=%d",
                            jobName, execution.getId(), execution.getJobInstance().getInstanceId()));
        }
        catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
                | JobParametersInvalidException | NoSuchJobException e) {
            result.setError(e);
            Throwable t = ExceptionUtils.getRootCause(e);
            if (t == null) t = e;
            result.setStatusMessage(format("Batch scheduled job failed: %s", t.getClass().getName()));
        }
        return result;
    }

}
