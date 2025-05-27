package com.rookiesvnclient.dto.svn;

public class SvnChangedPathDto {
    private String path;
    private String action;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
} 