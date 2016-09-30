package me.gking2224.common.batch;

import org.springframework.batch.core.ExitStatus;

public interface BatchConstants {

    // ATTRIBUTES
    String BATCH_DATE = "BATCH_DATE";
    String EXECUTION_TIME = "EXECUTION_TIME";
    String SKIPPED_ITEMS_LIST = "SKIPPED_ITEMS_LIST";
    String BAD_SUFFIX = "BAD_SUFFIX";
    String PROCESSED_SUFFIX = "PROCESSED_SUFFIX";
    String SEMAPHORE_SUFFIX = "SEMAPHORE_SUFFIX";
    
    // STATUS STRINGS
    String COMPLETED = "COMPLETED";
    String COMPLETED_WITH_SKIPS = "COMPLETED_WITH_SKIPS";
    String FILE_NOT_FOUND = "FAILED_FILE_NOT_FOUND";
    
    // EXIT STATUSES
    ExitStatus EXIT_STATUS_FILE_NOT_FOUND = new ExitStatus(FILE_NOT_FOUND);
    ExitStatus EXIT_STATUS_COMPLETED_WITH_SKIPS = new ExitStatus(COMPLETED_WITH_SKIPS);
}
