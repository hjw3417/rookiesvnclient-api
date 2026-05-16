# rookiesvnclient-api

> SVN 저장소의 변경 이력(log) · 디렉토리 트리 · 소스 비교를 통합 조회하고,
> 연관된 DB 프로시저/테이블 변경 이력까지 한 화면에서 시각화하기 위한 **Spring Boot 기반 백엔드 API**입니다.

내부망 전용 Electron 클라이언트(`rookiesvnclient`)와 짝을 이루는 서버 측 컴포넌트로,
복수의 SVN 서버(A, B …)와 복수의 DB 서버를 동일한 명세로 비교할 수 있도록 설계되었습니다.

---

## 📅 프로젝트 수행 기간

| 구분 | 일자 |
| --- | --- |
| 최초 커밋 (init) | **2025-05-03** |
| 최종 커밋 (백업용 commit) | **2025-05-28** |
| **총 수행 기간** | **2025-05-03 ~ 2025-05-28 (약 4주)** |

### 주요 마일스톤

| 일자 | 내용 |
| --- | --- |
| 2025-05-03 | 프로젝트 초기 셋업 (Spring Boot 3.4.5 / Java 17 / Gradle) |
| 2025-05-27 | ① 33·34번 서버 최상위 디렉토리 직하위 폴더명 조회 API (사이트 선택 popup 용)<br>② 폴더/파일 직하위 재귀 탐색 API<br>③ path(폴더명·파일명) + 사이트 기준 로그 조회 기능 추가 |
| 2025-05-28 | 백업용 커밋 (안정화) |

---

## 🧱 기술 스택

| 영역 | 사용 기술 |
| --- | --- |
| Language | Java 17 |
| Framework | Spring Boot 3.4.5 (Web · Security · Validation · Data JPA · DevTools) |
| Build Tool | Gradle (Wrapper 포함) |
| SVN 연동 | SVNKit 1.10.3 (`org.tmatesoft.svnkit`) |
| 인증 | JWT (`io.jsonwebtoken:jjwt 0.11.5`) + Spring Security |
| DB | MariaDB (`mariadb-java-client`) |
| Utility | Lombok 1.18.30 |
| Frontend 보조 | Bootstrap 5.3.6 · React-Bootstrap 2.10.10 (정적 자원용) |

---

## 🗂️ 디렉토리 구조

```
rookiesvnclient-api/
├── build.gradle              # 의존성 / 빌드 설정
├── settings.gradle
├── gradlew / gradlew.bat     # Gradle Wrapper
├── package.json              # 정적 프론트 라이브러리(Bootstrap)
├── CONTRIBUTING.md           # 프로젝트 가이드라인
└── src/
    └── main/
        ├── java/com/rookiesvnclient/
        │   ├── RookiesvnclientApplication.java   # 진입점
        │   ├── config/        # SecurityConfig, WebConfig, SvnProperties
        │   ├── cache/         # SvnCredentialCacheService (계정 캐싱)
        │   ├── jwt/           # JwtUtil, JwtAuthenticationFilter
        │   ├── svn/           # SvnConnectionManager (SVNKit 래퍼)
        │   ├── service/       # SvnReaderService, SvnRootService,
        │   │                  # SvnMergeService, LoginService
        │   ├── controller/    # LoginController, SvnController, SampleController
        │   └── dto/
        │       ├── auth/      # LoginRequestDto
        │       └── svn/       # SVN 응답/요청 DTO 묶음
        └── resources/
            └── application.properties
```

---

## ⚙️ 설정 (application.properties)

```properties
spring.application.name=rookiesvnclient-api

# DB
spring.datasource.url=jdbc:mariadb://localhost:3306/rookiesvnclient
spring.datasource.username=root
spring.datasource.password=********
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true

# SVN 서버 (다중 등록)
svn.servers.A=${SVN_SERVER_A_URL}
svn.servers.B=${SVN_SERVER_B_URL}
```

> ⚠️ 실서비스에서는 DB 비밀번호 · SVN URL · JWT Secret 등을 반드시 **환경변수** 또는 외부 설정으로 분리하세요.

---

## 🔐 인증 흐름

1. 클라이언트가 `POST /api/login` 으로 `username` / `password` 전송
2. `LoginService` 가 **등록된 모든 SVN 서버에 대해** 인증 시도
3. 모두 성공 시 `SvnCredentialCacheService` 에 자격증명 캐시 → JWT 발급
4. 이후 모든 SVN API 호출 시 `Authorization: Bearer <token>` 헤더 필요
5. `JwtAuthenticationFilter` 가 토큰 검증 후 `SecurityContext` 에 사용자 등록

---

## 📡 주요 API

| Method | Endpoint | 설명 |
| --- | --- | --- |
| `POST` | `/api/login` | 다중 SVN 서버 통합 로그인, JWT 발급 |
| `GET`  | `/api/svn/roots` | 등록된 모든 SVN 서버의 **최상위 디렉토리** 목록 조회 (사이트 선택 popup용) |
| `GET`  | `/api/svn/tree?server={A\|B}&basePath=` | 지정 서버 / 경로 기준 디렉토리 트리 조회 |
| `GET`  | `/api/svn/children?roots=...&path=...` | 여러 root 의 동일 path 직하위를 **MERGE** 하여 비교 가능 형태로 반환 |
| `POST` | `/api/svn/logs` | `rootNames` + `path` 기준 **서버별 로그 리스트** 조회 |

### 요청 예시 – 로그 조회

```http
POST /api/svn/logs
Authorization: Bearer <JWT>
Content-Type: application/json

{
  "rootNames": ["SMMES", "EMFOCUS"],
  "path": ["trunk/src/some/file.java"]
}
```

---

## 🚀 실행 방법

### 1) 사전 준비
- JDK 17
- MariaDB (DB 명: `rookiesvnclient`)
- 접근 가능한 SVN 서버 (`application.properties` 의 `svn.servers.*`)

### 2) 빌드 & 실행

```bash
# 빌드
./gradlew clean build

# 개발 실행 (devtools 자동 적용)
./gradlew bootRun

# 또는 JAR 실행
java -jar build/libs/rookiesvnclient-api-0.0.1-SNAPSHOT.jar
```

### 3) 테스트

```bash
./gradlew test
```

---

## 🧭 설계 원칙 (CONTRIBUTING.md 발췌)

- **최소한의 코드**로 구현 · 불필요한 코드 제거
- SVN 접근은 **SVNKit** 으로 일원화
- 파일 Diff 는 `diff-match-patch`, `react-diff-viewer` 활용 (클라이언트 측)
- 내부망 전용 · 고정 IP 접근 제한 · 민감정보는 환경변수로 관리
- Java 는 **Javadoc**, SQL 은 **실행 계획·목적**, React 컴포넌트는 **Props 타입**을 명시

---

## 🛣️ 향후 확장 포인트

- 다중 DB 서버(A·B·C) 간 동일 명칭 `Procedure`/`Table`/`Function`/`Trigger` Row 단위 비교
- SVN 로그 ↔ DB 변경 이력 매핑 (커밋 → 스키마 변경 추적)
- Electron 클라이언트(`rookiesvnclient`) 와의 패키징 통합

---

## 📄 License

사내 프로젝트 (내부망 전용). 별도 라이선스 명시 전까지 외부 배포 금지.
