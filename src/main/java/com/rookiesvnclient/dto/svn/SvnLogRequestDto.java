package com.rookiesvnclient.dto.svn;

import java.util.List;

public class SvnLogRequestDto {
    private List<String> rootNames;
    private List<String> path;
    private Integer limit;

    public List<String> getRootNames() {
        return rootNames;
    }

    public void setRootNames(List<String> rootNames) {
        this.rootNames = rootNames;
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
} 