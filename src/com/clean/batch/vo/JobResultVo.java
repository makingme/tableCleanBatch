package com.clean.batch.vo;

public class JobResultVo {
    private final String jobId;
    private final int deleteCount;
    private final long executionTime;

    public JobResultVo(String jobId, int deleteCount, long executionTime) {
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
