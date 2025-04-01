package com.clean.batch.extra;

import com.clean.batch.vo.CleanJobConfigVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.List;

public class CleanJob {
    private final static Logger logger = LoggerFactory.getLogger("MAIN");

    private final String jobId;
    private final CleanJobConfigVo jobConfigVo;

    public CleanJob(String jobId, CleanJobConfigVo cleanJobConfigVo) {
        this.jobId= jobId;
        this.jobConfigVo = cleanJobConfigVo;
    }

    public int execute() throws SQLException {
        int deleteCount =0;
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            deleteCount = deleteInBatchExecute(conn);
        } catch (SQLException e) {
            throw new SQLException(jobId+" 수행 중 에러 발생", e);
        }
        return deleteCount;
    }

    private int deleteInBatchExecute(Connection conn) throws SQLException {
        int batchCount = 0;
        try (PreparedStatement selectStmt = conn.prepareStatement(jobConfigVo.getSelectQuery());
             PreparedStatement deleteStmt = conn.prepareStatement(jobConfigVo.getDeleteQuery())) {

            while (batchCount < jobConfigVo.getMaxBatchCount() && !Thread.currentThread().isInterrupted()) {
                long startTime = System.currentTimeMillis();
                try(ResultSet rs = selectStmt.executeQuery()) {

                    int pkCount = 0;
                    deleteStmt.clearBatch();
                    List<String> pkList = jobConfigVo.getPkColumnList();
                    while (rs.next()) {
                        for(int i=0; i<pkList.size(); i++){
                            deleteStmt.setObject(i+1, rs.getObject(pkList.get(i)));
                        }
                        deleteStmt.addBatch();
                        pkCount++;
                    }
                    logger.info("{} JOB 조회 완료, 건수:{}, 소요 시간(ms):{}", jobId, pkCount, System.currentTimeMillis()-startTime);
                    startTime = System.currentTimeMillis();
                    if (pkCount > 0) {
                        deleteStmt.executeBatch();
                        conn.commit();
                        batchCount = batchCount + pkCount;
                        logger.info("{} JOB 삭제 완료 - 총 삭제 건수:{}, 삭제 소요시간(ms):{}", jobId, batchCount, System.currentTimeMillis() - startTime);
                    } else {
                        logger.info("{} JOB 건수 없음으로 종료 - 총 삭제 건수:{}", jobId, batchCount);
                        break;
                    }
                }
            }
        }catch (SQLException e) {
            conn.rollback(); // 에러 발생 시 롤백
            throw e;
        }
        return batchCount;
    }

    public String getJobId() { return jobId; }
}


