package com.rookiesvnclient.controller;

import com.rookiesvnclient.dto.SvnFileTreeNode;
import com.rookiesvnclient.service.SvnReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.tmatesoft.svn.core.SVNException;

/**
 * SVN 디렉토리 트리 구조를 조회하는 컨트롤러
 */
@RestController
@RequestMapping("/api/svn")
@RequiredArgsConstructor
public class SvnController {

    private final SvnReaderService svnReaderService;

    /**
     * 주어진 경로 기준으로 SVN 디렉토리 트리 구조를 조회합니다.
     *
     * @param basePath 시작할 경로 (예: "", "SMMES")
     * @return 트리 구조의 루트 노드
     * @throws SVNException SVN 조회 실패 시
     */
    @GetMapping("/tree")
    public SvnFileTreeNode getSvnDirectoryTree(@RequestParam(defaultValue = "") String basePath) throws SVNException {
        return svnReaderService.buildDirectoryTree(basePath);
    }
}
