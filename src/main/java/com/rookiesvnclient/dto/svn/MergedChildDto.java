package com.rookiesvnclient.dto.svn;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MergedChildDto {
    private String name;        // 파일/폴더명
    private String type;        // "file" or "directory"
    private List<String> servers; // 존재하는 서버 식별자 리스트
} 