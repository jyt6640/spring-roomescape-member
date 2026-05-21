# Room Escape BE

방탈출 예약 관리와 사용자 예약 기능을 제공하는 Spring Boot 백엔드 API입니다.

## 기능 목록

- 예약 관리 API
  - 전체 예약 조회
  - 관리자 예약 생성
  - 관리자 예약 삭제
- 예약 시간 관리 API
  - 시간 생성
  - 시간 조회
  - 예약이 없는 시간 삭제
- 테마 관리 API
  - 테마 생성
  - 테마 조회
  - 예약이 없는 테마 삭제
- 사용자 예약 API
  - 날짜와 테마 기준 예약 가능 시간 조회
  - 사용자 예약 생성
  - 이름 기준 내 예약 조회
  - 내 예약 날짜/시간 변경
  - 내 예약 취소
- 인기 테마 API
  - 오늘을 제외한 최근 1주 게임 날짜 기준 예약 수 상위 10개 테마 조회
- 에러 처리
  - 유효하지 않은 입력, 존재하지 않는 리소스, 중복 예약, 정책 위반을 일관된 JSON 응답으로 반환

## API 명세

### 관리자 예약

- `GET /reservations`
- `POST /reservations`
  - 요청: `{ "name": "브라운", "date": "2026-05-22", "timeId": 1, "themeId": 1 }`
  - 이전 단계 호환 요청: `{ "name": "브라운", "date": "2023-08-05", "time": "15:40" }`
- `DELETE /reservations/{id}`

### 예약 시간

- `GET /times`
- `POST /times`
  - 요청: `{ "startAt": "10:00" }`
- `DELETE /times/{id}`

### 테마

- `GET /themes`
- `POST /themes`
  - 요청: `{ "name": "공포의 방", "description": "어두운 저택", "thumbnailUrl": "https://example.com/horror.png" }`
- `DELETE /themes/{id}`
- `GET /api/themes/popular`

### 사용자 예약

- `GET /api/reservation-times/available?date=2026-05-22&themeId=1`
- `POST /api/reservations`
  - 요청: `{ "name": "브라운", "date": "2026-05-22", "timeId": 1, "themeId": 1 }`
- `GET /api/reservations?name=브라운`
- `PATCH /api/reservations/{id}?name=브라운`
  - 요청: `{ "date": "2026-05-23", "timeId": 2 }`
- `DELETE /api/reservations/{id}?name=브라운`

## 에러 응답

```json
{
  "code": "DUPLICATED_RESERVATION",
  "message": "이미 예약된 날짜, 시간, 테마입니다."
}
```

상태 코드는 입력 오류 `400`, 권한 불일치 `403`, 리소스 없음 `404`, 중복/삭제 불가 `409`로 구분했다. 사용자가 다음 행동을 알 수 있도록 `message`는 화면에 바로 노출 가능한 한국어 문장으로 작성했다.

## API 설계 결정 기록

- 관리자 API(`/reservations`)와 사용자 API(`/api/reservations`)를 분리했다. 관리자는 현장 예약을 보정할 수 있어야 하고, 사용자는 서비스 정책을 강하게 적용받아야 하기 때문이다.
- 예약 가능 시간은 `/api/reservation-times/available`로 분리했다. 사용자가 예약을 만들기 전에 선택지를 조회하는 읽기 전용 리소스이기 때문이다.
- 사용자는 로그인 없이 이름으로 식별된다는 요구사항을 반영해 내 예약 조회/변경/취소에서 `name`을 사용한다.
- 과거 단계 학습 테스트 호환을 위해 관리자 예약 생성은 `time` 문자열 요청도 받는다. 새 사용자 흐름은 `timeId`, `themeId`만 사용한다.

## 미션 중 기록

- 규칙 적용 코드: 사용자 예약 생성과 변경에서 `date + time + theme` 중복을 서비스 계층에서 검사하도록 변경했다. 같은 시간이라도 테마가 다르면 예약 가능하다는 API 규칙을 코드에 반영했다.
- 테스트 작성이 어려웠던 코드: 인기 테마 조회는 "오늘" 기준 최근 1주라는 시간이 걸려 있어 고정 테스트 데이터가 필요했다. `src/test/resources/data.sql`을 만들고 해당 테스트에서만 실행하도록 구성했다.
- 막힌 순간: 이전 단계의 `/reservations` 요청은 `time` 문자열을 사용하지만 새 요구사항은 `timeId` 객체 관계를 요구했다. 관리자 API는 호환성을 유지하고 사용자 API는 새 명세만 받도록 분리했다.
- 변경/취소 엣지 케이스: 이미 지난 예약 취소/변경, 변경하려는 날짜+시간+테마 중복, 다른 이름으로 취소 시도를 각각 `400`, `409`, `403`으로 처리했다.
- 유지/수정/폐기한 규칙: REST 리소스 중심 URL은 유지했고, 모든 정책을 컨트롤러에 두지 않는 규칙을 강화했다. 단순 학습 단계의 문자열 시간 저장 규칙은 폐기하고 시간 테이블 참조로 전환했다.
