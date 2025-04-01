package com.clean.batch.vo;

import java.util.ArrayList;
import java.util.List;

public class CleanJobConfigVo {

    private String selectQuery;
    private String deleteQuery;
    private List<String> pkColumnList = new ArrayList<>();
    private int maxBatchCount = 1000000;

    public String getDeleteQuery() { return deleteQuery; }
    public void setDeleteQuery(String deleteQuery) { this.deleteQuery = deleteQuery; }

    public String getSelectQuery() { return selectQuery; }
    public void setSelectQuery(String selectQuery) { this.selectQuery = selectQuery; }

    public List<String> getPkColumnList() { return pkColumnList; }
    public void setPkColumnList(List<String> pkColumnList) { this.pkColumnList = pkColumnList; }

    public int getMaxBatchCount() { return maxBatchCount; }
    public void setMaxBatchCount(int maxBatchCount) { this.maxBatchCount = maxBatchCount; }
}
