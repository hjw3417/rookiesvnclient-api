package com.rookiesvnclient.dto.svn;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * SVN 서버별 루트(최상위) 디렉토리 조회 결과 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SvnRootResponseDto {
    private boolean success;      // 성공 여부
    private List<String> items;   // 파일/폴더 이름 리스트 (성공 시)
    private String message;       // 에러 메시지 또는 안내 메시지 (실패 시)
} 