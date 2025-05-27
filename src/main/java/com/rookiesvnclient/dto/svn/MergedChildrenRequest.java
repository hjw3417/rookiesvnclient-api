package com.rookiesvnclient.dto.svn;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MergedChildrenRequest {
    private List<String> roots; // 선택된 프로젝트명(들)
    private String path; // 조회할 상대 경로
} 