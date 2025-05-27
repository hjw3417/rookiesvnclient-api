package com.rookiesvnclient.dto.svn;

import java.util.List;

public class SvnLogEntryDto {
    private long revision;
    private String author;
    private String date;
    private String message;
    private List<SvnChangedPathDto> changedPaths;

    public long getRevision() {
        return revision;
    }

    public void setRevision(long revision) {
        this.revision = revision;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<SvnChangedPathDto> getChangedPaths() {
        return changedPaths;
    }

    public void setChangedPaths(List<SvnChangedPathDto> changedPaths) {
        this.changedPaths = changedPaths;
    }
} 