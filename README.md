# spring-roomescape-member

## 기능 목록

- 예약 관리
    - 관리자는 전체 예약을 조회한다.
    - 관리자는 예약자 이름, 날짜, 시간으로 예약을 등록한다.
    - 관리자는 예약을 삭제한다.
    - 예약은 DB가 발급한 식별자를 가진다.
- 시간 관리
    - 관리자는 예약 시간을 추가한다.
    - 관리자는 예약 시간 목록을 조회한다.
    - 관리자는 예약이 없는 시간을 삭제한다.
- 테마 관리
    - 관리자는 테마를 추가한다.
    - 관리자는 테마 목록을 조회한다.
    - 관리자는 테마를 삭제한다.
- 사용자 예약
    - 사용자는 날짜와 테마를 선택해 예약 가능한 시간을 조회한다.
    - 사용자는 이름, 날짜, 시간, 테마로 예약한다.
    - 같은 날짜와 시간이라도 테마가 다르면 예약할 수 있다.
    - 사용자는 이름으로 본인 예약을 조회한다.
    - 사용자는 본인 예약의 날짜와 시간을 변경한다.
    - 사용자는 본인 예약을 취소한다.
- 정책과 에러
    - 사용자는 지나간 날짜와 시간에 예약할 수 없다.
    - 같은 날짜, 시간, 테마의 중복 예약을 거부한다.
    - 예약이 있는 시간은 삭제할 수 없다.
    - 빈 이름과 잘못된 날짜/시간 형식은 사용자 메시지로 응답한다.
    - 의도한 에러는 500이 아니라 정해진 상태 코드와 에러 본문으로 응답한다.
- 인기 테마
    - 최근 1주 동안 예약이 많았던 테마 상위 10개를 조회한다.

## API 명세

### 예약 관리 API

| 기능 | Method | URL | 요청 | 응답 |
| --- | --- | --- | --- | --- |
| 예약 조회 | GET | `/reservations` | - | `[{id,name,date,time,theme}]` |
| 관리자 예약 추가 | POST | `/reservations` | `{name,date,time}` 또는 `{name,date,timeId,themeId}` | `{id,name,date,time,theme}` |
| 관리자 예약 삭제 | DELETE | `/reservations/{id}` | - | `200 OK` |
| 예약 가능 시간 조회 | GET | `/reservations/available-times?date=yyyy-MM-dd&themeId=1` | - | `[{id,startAt}]` |

### 사용자 예약 API

| 기능 | Method | URL | 요청 | 응답 |
| --- | --- | --- | --- | --- |
| 사용자 예약 추가 | POST | `/users/reservations` | `{name,date,timeId,themeId}` | `{id,name,date,time,theme}` |
| 내 예약 조회 | GET | `/users/reservations?name=브라운` | - | `[{id,name,date,time,theme}]` |
| 내 예약 변경 | PATCH | `/users/reservations/{id}` | `{name,date,timeId}` | `{id,name,date,time,theme}` |
| 내 예약 취소 | DELETE | `/users/reservations/{id}?name=브라운` | - | `200 OK` |

### 시간 API

| 기능 | Method | URL | 요청 | 응답 |
| --- | --- | --- | --- | --- |
| 시간 추가 | POST | `/times` | `{startAt}` | `{id,startAt}` |
| 시간 조회 | GET | `/times` | - | `[{id,startAt}]` |
| 시간 삭제 | DELETE | `/times/{id}` | - | `200 OK` |

### 테마 API

| 기능 | Method | URL | 요청 | 응답 |
| --- | --- | --- | --- | --- |
| 테마 추가 | POST | `/themes` | `{name,description,thumbnail}` | `{id,name,description,thumbnail}` |
| 테마 조회 | GET | `/themes` | - | `[{id,name,description,thumbnail}]` |
| 테마 삭제 | DELETE | `/themes/{id}` | - | `200 OK` |
| 인기 테마 조회 | GET | `/themes/popular` | - | `[{id,name,description,thumbnail,reservationCount}]` |

## 에러 응답

```json
{
  "code": "RESERVATION_DUPLICATED",
  "message": "이미 예약된 시간입니다."
}
```

| 상황 | 상태 코드 | code |
| --- | --- | --- |
| 빈 예약자 이름 | 400 | `RESERVATION_INVALID_NAME` |
| 잘못된 날짜 형식 | 400 | `RESERVATION_INVALID_DATE` |
| 잘못된 시간 형식 | 400 | `TIME_INVALID_START_AT` |
| 과거 예약 생성 | 400 | `RESERVATION_PAST` |
| 중복 예약 | 400 | `RESERVATION_DUPLICATED` |
| 예약이 있는 시간 삭제 | 400 | `TIME_RESERVED` |
| 존재하지 않는 예약 | 404 | `RESERVATION_NOT_FOUND` |
| 존재하지 않는 시간 | 404 | `TIME_NOT_FOUND` |
| 존재하지 않는 테마 | 404 | `THEME_NOT_FOUND` |
| 다른 사람의 예약 변경/취소 | 403 | `RESERVATION_OWNER_MISMATCH` |

## API 설계 결정 기록

- 관리자 예약 API와 사용자 예약 API를 분리했다. 관리자는 기존 단계 미션의 전화·현장 등록 흐름을 유지하고, 사용자는 서비스 정책을 적용한 별도 진입점으로 예약한다.
- 내 예약 변경/취소는 로그인 없이 이름으로 소유자를 확인한다. 미션 조건상 별도 인증이 없으므로, 이름을 최소 식별 정보로 사용한다.
- 예약 가능 시간은 `/reservations/available-times`로 두었다. 예약이라는 자원에서 파생되는 조회이며, 날짜와 테마 조건이 바뀌어도 같은 조회 의미를 유지한다.
- 에러 응답은 `{code,message}`로 고정했다. 브라우저는 `message`를 사용자에게 보여주고, 테스트와 클라이언트 분기는 `code`를 기준으로 삼을 수 있다.
- `data.sql`은 실제 앱 확인용 초기 데이터를 넣는다. 테스트는 단계별 빈 상태 검증을 위해 테스트 전용 `test-data.sql`을 사용한다.

## 미션 중 기록

- 규칙을 적용해서 변경한 코드: `ReservationController`에서 `JdbcTemplate`을 제거하고 `ReservationService`에 흐름을 위임했다. DB 접근은 `JdbcTemplateReservationRepository`가 담당한다.
- 테스트 작성이 어려웠던 코드: 인기 테마는 "최근 1주"라는 현재 날짜 의존성이 있어 고정 날짜 fixture가 쉽게 깨진다. 테스트에서는 `LocalDate.now().minusDays(1)`로 기준일에 맞는 데이터를 직접 넣어 검증했다.
- 막힌 순간: 기존 단계 테스트는 `time` 문자열을 보내고, 최종 요구사항은 `timeId`를 보낸다. 관리자 예약 생성에서 두 입력을 모두 받아들이고, 사용자 예약 생성은 `timeId`만 정책 검증하도록 분리했다.
- 변경/취소 엣지 케이스: 이미 지난 예약은 변경/취소를 거부하고, 변경하려는 날짜·시간·테마가 이미 예약된 경우 중복 예약으로 거부한다.
- 유지/수정/폐기한 규칙: 도메인 우선 패키지와 DTO 분리는 유지했다. 단, 외부 요구사항 테스트 호환을 위해 루트 패키지에 역직렬화용 `Reservation` 클래스를 별도로 두었다.
