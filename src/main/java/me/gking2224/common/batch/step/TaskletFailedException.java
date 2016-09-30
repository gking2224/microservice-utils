package me.gking2224.common.batch.step;

public class TaskletFailedException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -7059752422890575002L;

    public TaskletFailedException() {
        super();
    }

    public TaskletFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskletFailedException(String message) {
        super(message);
    }

    public TaskletFailedException(Throwable cause) {
        super(cause);
    }
    
    

}
