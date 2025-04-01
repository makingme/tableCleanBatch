package com.clean.batch.extra;

public class JobResult {
    private final String jobId;
    private final int deleteCount;
    private final long executionTime;

    public JobResult(String jobId, int deleteCount, long executionTime) {
        this.jobId = jobId;
        this.deleteCount = deleteCount;
        this.executionTime = executionTime;
    }

    public String getJobId() {
        return jobId;
    }

    public int getDeleteCount() {
        return deleteCount;
    }

    public long getExecutionTime() {
        return executionTime;
    }
}
