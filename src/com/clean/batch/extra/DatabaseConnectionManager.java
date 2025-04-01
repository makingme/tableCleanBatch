package com.clean.batch.extra;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionManager {
    private static DatabaseConnectionManager instance;
    private HikariDataSource dataSource;

    private DatabaseConnectionManager() {
        // 싱글톤 패턴
    }

    public static synchronized DatabaseConnectionManager getInstance() {
        if (instance == null) {
            instance = new DatabaseConnectionManager();
        }
        return instance;
    }

    public void initialize(JdbcInfoVo jdbcInfoVo) throws IllegalArgumentException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcInfoVo.getJdbcUrl());
        config.setUsername(jdbcInfoVo.getUsername());
        config.setPassword(jdbcInfoVo.getPassword());

        // 커넥션 풀 기본 설정
        config.setMaximumPoolSize(jdbcInfoVo.getMaximumPoolSize());
        config.setMinimumIdle(jdbcInfoVo.getMinimumIdl());
        config.setIdleTimeout(jdbcInfoVo.getIdleTimeout());
        config.setMaxLifetime(jdbcInfoVo.getMaxLifetime());
        config.setConnectionTimeout(jdbcInfoVo.getConnectionTimeout());
        if(jdbcInfoVo.getPoolName() !=null && !jdbcInfoVo.getPoolName().isEmpty()){
            config.setPoolName("HikariPool-" + jdbcInfoVo.getPoolName());
        }else{
            config.setPoolName("HikariPool-" + jdbcInfoVo.getJdbcUrl().hashCode());
        }
        if(jdbcInfoVo.getConnectionTestQuery() !=null && !jdbcInfoVo.getConnectionTestQuery().isEmpty()){
            // 커넥션 테스트 쿼리 설정
            config.setConnectionTestQuery(jdbcInfoVo.getConnectionTestQuery());
        }

        dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("데이터소스가 초기화되지 않았습니다.");
        }
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
