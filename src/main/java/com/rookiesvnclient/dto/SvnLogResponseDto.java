package com.rookiesvnclient.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class SvnLogResponseDto {

    private String folderPath;    // 폴더 경로
    private long revision;        // 리비전 번호
    private String author;        // 작성자
    private String message;       // 커밋 메시지
    private Date committedAt;     // 커밋 시간
}
