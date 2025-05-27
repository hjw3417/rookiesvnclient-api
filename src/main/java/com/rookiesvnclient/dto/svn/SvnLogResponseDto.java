package com.rookiesvnclient.dto.svn;

import java.util.List;
import java.util.Map;

public class SvnLogResponseDto {
    private Map<String, List<SvnLogEntryDto>> logsByRoot;

    public Map<String, List<SvnLogEntryDto>> getLogsByRoot() {
        return logsByRoot;
    }

    public void setLogsByRoot(Map<String, List<SvnLogEntryDto>> logsByRoot) {
        this.logsByRoot = logsByRoot;
    }
} 