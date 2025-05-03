package com.rookiesvnclient.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SvnLogRequestDto {

    private List<String> paths; // 조회할 폴더 리스트
    private int limit;          // 조회할 개수

}
