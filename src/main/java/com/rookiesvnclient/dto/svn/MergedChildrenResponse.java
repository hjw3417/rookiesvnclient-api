package com.rookiesvnclient.dto.svn;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MergedChildrenResponse {
    private String rootPath; // 프로젝트명
    private List<MergedChildDto> children; // 하위 목록
} 