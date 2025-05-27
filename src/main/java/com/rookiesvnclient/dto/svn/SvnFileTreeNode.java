package com.rookiesvnclient.dto.svn;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * SVN 디렉토리/파일 트리 구조를 표현하는 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SvnFileTreeNode {
    private String name;                   // 파일 또는 폴더 이름
    private String path;                   // 전체 경로
    private boolean directory;             // 디렉토리 여부
    private List<SvnFileTreeNode> children; // 하위 항목 목록 (디렉토리일 경우)

    /**
     * 이름과 디렉토리 여부만으로 노드를 생성할 때 사용하는 생성자
     * @param entryName 파일/디렉토리 이름
     * @param isDir 디렉토리 여부
     */
    public SvnFileTreeNode(String entryName, boolean isDir) {
        this.name = entryName;
        this.directory = isDir;
        this.path = ""; // 이후 트리 생성 중에 설정될 수 있음
        this.children = isDir ? new ArrayList<>() : null;
    }
}
