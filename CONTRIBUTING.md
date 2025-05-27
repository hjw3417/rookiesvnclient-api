# SVN 로그, 소스 비교, DB 변경 이력 시각화 시스템 가이드라인

## 1. 프로젝트 방향성
- SVN 저장소의 파일/폴더 변경 이력(log) 조회
- 파일 소스 비교(Diff 툴 활용)
- 연관된 DB 프로시저/테이블 변경 이력 통합 조회 및 시각화
- 여러 DB 서버(A, B, C) 간 동일 이름의 Procedure, Table 비교

## 2. 코드 작성 원칙
- 최소한의 코드로 구현
- 불필요한 코드 제거
- 명확한 주석 작성
- SVNKit 라이브러리 활용
- Diff 라이브러리(diff-match-patch, react-diff-viewer) 활용

## 3. 응답 원칙
- 질문에 대해 명확한 예/아니오로 답변
- 간결하고 명확한 설명
- 불필요한 장황함 제거

## 4. 문서화
- Java: Javadoc 형식
- SQL: 실행 계획 및 목적 명시
- TypeScript/React: 컴포넌트 설명 및 Props 타입 정의

## 5. 보안
- 내부망 전용 Electron 애플리케이션
- 고정 IP 접근 제한
- 민감 정보 환경변수 관리
- TortoiseSVN RepoBrowser 접근 권한 관리

## 6. 개발 프로세스
1. Spring Boot 프로젝트 생성
   - SvnReaderService.java
   - FileComparerService.java
   - DbProcedureTrackerService.java
   - ApiController.java
2. SVN 접근 로직 구현
3. XML 로그 파싱 및 파일 소스 읽기
4. 파일 변경 비교 로직 구현
5. DB 연동 및 로그 저장
6. React + TypeScript 프론트 개발
7. Electron 패키징

## 7. 디렉토리 구조
```
src/
├── main/
│   ├── java/        # 백엔드 소스
│   │   ├── service/     # SVN, DB 관련 서비스
│   │   ├── controller/  # API 엔드포인트
│   │   └── dto/         # 데이터 전송 객체
│   ├── resources/   # 설정 파일
│   └── webapp/      # 프론트엔드 소스
└── test/            # 테스트 코드
```

## 8. 주요 기능
### SVN 로그 조회
- 디렉토리 트리 구조 조회
- 로그 조회 및 비교
- 유사 구조 프로젝트 간 비교

### DB 요소 비교
- 다중 DB 서버 간 동일 명칭 요소 비교
- Table, Procedure, Function, Trigger 비교
- Row 단위 차이점 분석

## 9. 질문 및 피드백
- 구현 전 방향성 확인
- 누락된 요구사항 검토
- 보안 및 성능 고려사항 확인
- 라이브러리 추천 요청 