package com.rookiesvnclient.controller;

import com.rookiesvnclient.dto.svn.SvnFileTreeNode;
import com.rookiesvnclient.dto.svn.SvnRootResponseDto;
import com.rookiesvnclient.service.SvnReaderService;
import com.rookiesvnclient.service.SvnRootService;
import com.rookiesvnclient.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.tmatesoft.svn.core.SVNException;
import com.rookiesvnclient.dto.svn.MergedChildrenRequest;
import com.rookiesvnclient.dto.svn.MergedChildDto;
import com.rookiesvnclient.service.SvnMergeService;
import com.rookiesvnclient.dto.svn.MergedChildrenResponse;
import com.rookiesvnclient.dto.svn.SvnLogRequestDto;
import com.rookiesvnclient.dto.svn.SvnLogResponseDto;
import com.rookiesvnclient.dto.svn.SvnLogEntryDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Collections;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * SVN 디렉토리 트리 구조를 조회하는 컨트롤러
 */
@RestController
@RequestMapping("/api/svn")
@RequiredArgsConstructor
public class SvnController {

    private final SvnReaderService svnReaderService;
    private final SvnRootService svnRootService;
    private final JwtUtil jwtUtil;
    private final SvnMergeService svnMergeService;

    /**
     * 주어진 경로 기준으로 SVN 디렉토리 트리 구조를 조회합니다.
     *
     * @param server 서버 식별자 (예: "A", "B")
     * @param basePath 시작할 경로 (예: "", "SMMES")
     * @param username 로그인 사용자 (JWT에서 추출된 인증 주체)
     * @return 트리 구조의 루트 노드
     */
    @GetMapping("/tree")
    public SvnFileTreeNode getSvnDirectoryTree(
            @RequestParam String server,
            @RequestParam(defaultValue = "") String basePath,
            @AuthenticationPrincipal(expression = "username") String username
    ) throws SVNException {
        return svnReaderService.buildDirectoryTree(server, basePath, username);
    }

    /**
     * 모든 SVN 서버의 루트(최상위) 디렉토리 파일/폴더 이름을 반환
     * @param authHeader Authorization 헤더 (Bearer 토큰)
     * @return 서버별 결과 Map
     */
    @GetMapping("/roots")
    public ResponseEntity<Map<String, List<String>>> getRootDirectories(@RequestHeader("Authorization") String authHeader) throws SVNException {
        System.out.println("[SvnController] getRootDirectories 호출됨");
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.validateAndExtractUsername(token);
        Map<String, List<String>> result = new LinkedHashMap<>();
        List<String> servers = svnRootService.getAllServerNames();
        for (String server : servers) {
            try {
                List<String> roots = svnReaderService.getTopLevelDirectories(server, username);
                result.put(server, roots);
                System.out.println("server: " + server + " roots: " + roots);
            } catch (Exception e) {
                result.put(server, List.of());
                System.err.println("server: " + server + " 오류: " + e.getMessage());
            }
        }
        return ResponseEntity.ok(result);
    }

    /**
     * roots(여러 프로젝트명)와 path를 받아 해당 경로의 하위 디렉토리 MERGE 조회
     */
    @GetMapping("/children")
    public ResponseEntity<List<MergedChildrenResponse>> getChildren(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam List<String> roots,
            @RequestParam(defaultValue = "") String path
    ) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.validateAndExtractUsername(token);
        MergedChildrenRequest req = new MergedChildrenRequest(roots, path);
        System.out.println("[SvnController] getChildren 호출됨: roots=" + roots + ", path=" + path);
        List<MergedChildrenResponse> result = svnMergeService.getMergedChildren(username, req);
        return ResponseEntity.ok(result);
    }

    /**
     * 여러 root와 path를 받아 root별 로그 리스트를 반환
     */
    @PostMapping("/logs")
    public ResponseEntity<SvnLogResponseDto> getLogsByRootsAndPath(
            @RequestBody SvnLogRequestDto request,
            @RequestHeader("Authorization") String authHeader
    ) {
        System.out.println("[SvnController] getLogsByRootsAndPath 호출됨");
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.validateAndExtractUsername(token);
        System.out.println("[JwtAuthFilter] method: " + request.getPath());
        System.out.println("[JwtAuthFilter] AuthHeader: " + authHeader);
        System.out.println("[JwtAuthFilter] SecurityContext before: " + SecurityContextHolder.getContext().getAuthentication());
        try {
            Map<String, List<SvnLogEntryDto>> logsByRoot = svnReaderService.getLogsByRootsAndPath(
                    request.getRootNames(),
                    (request.getPath() != null && !request.getPath().isEmpty() ? request.getPath().get(0) : ""),
                    username
            );
            SvnLogResponseDto response = new SvnLogResponseDto();
            response.setLogsByRoot(logsByRoot);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
