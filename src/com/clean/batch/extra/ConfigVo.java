package com.clean.batch.extra;

import java.util.HashMap;
import java.util.Map;

public class ConfigVo {

    private JdbcInfoVo jdbcInfo;
    private String encryptClass;
    private long   cycle = 60000;
    private String startTime ;
    private String endTime;
    private Map<String, CleanJobConfigVo> cleanJob = new HashMap<String, CleanJobConfigVo>(5);

    public JdbcInfoVo getJdbcInfo() { return jdbcInfo; }
    public void setJdbcInfo(JdbcInfoVo jdbcInfo) { this.jdbcInfo = jdbcInfo; }

    public String getEncryptClass() { return encryptClass; }
    public void setEncryptClass(String encryptClass) { this.encryptClass = encryptClass; }

    public long getCycle() { return cycle; }
    public void setCycle(long cycle) { this.cycle = cycle; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public Map<String, CleanJobConfigVo> getCleanJob() { return cleanJob; }
    public void setCleanJob(Map<String, CleanJobConfigVo> cleanJob) { this.cleanJob = cleanJob; }
}
