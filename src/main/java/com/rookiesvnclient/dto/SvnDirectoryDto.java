package com.rookiesvnclient.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SvnDirectoryDto {

    private String name;              // 폴더명
    private String path;              // 전체 경로
    private List<SvnDirectoryDto> children; // 하위 폴더들

}
