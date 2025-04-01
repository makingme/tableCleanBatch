package com.clean.batch.vo;

public class JdbcInfoVo {
    private String jdbcUrl;
    private String username;
    private String password;

    private int maximumPoolSize = 5;
    private int minimumIdl = 5;

    private long idleTimeout = 5 * 60 * 1000;

    private long maxLifetime = 30 * 60 * 1000;

    private long connectionTimeout = 30 * 1000;

    private String connectionTestQuery;

    private String poolName;

    public String getJdbcUrl() { return jdbcUrl; }
    public void setJdbcUrl(String jdbcUrl) { this.jdbcUrl = jdbcUrl; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public long getConnectionTimeout() { return connectionTimeout; }
    public void setConnectionTimeout(long connectionTimeout) { this.connectionTimeout = connectionTimeout; }

    public long getIdleTimeout() { return idleTimeout; }
    public void setIdleTimeout(long idleTimeout) { this.idleTimeout = idleTimeout; }

    public int getMaximumPoolSize() { return maximumPoolSize; }
    public void setMaximumPoolSize(int maximumPoolSize) { this.maximumPoolSize = maximumPoolSize; }

    public long getMaxLifetime() { return maxLifetime; }
    public void setMaxLifetime(long maxLifetime) { this.maxLifetime = maxLifetime; }

    public int getMinimumIdl() { return minimumIdl; }
    public void setMinimumIdl(int minimumIdl) { this.minimumIdl = minimumIdl; }

    public String getConnectionTestQuery() { return connectionTestQuery; }
    public void setConnectionTestQuery(String connectionTestQuery) { this.connectionTestQuery = connectionTestQuery; }

    public String getPoolName() { return poolName; }
    public void setPoolName(String poolName) { this.poolName = poolName; }
}
