package roomescape.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserReservationApiTest {

    @Test
    @DisplayName("예약 가능 시간 조회 후 예약하면 해당 시간이 목록에서 빠진다")
    void findAvailableTimes_success_after_create_reservation() {
        // given
        Long firstTimeId = createTime("10:00");
        Long secondTimeId = createTime("11:00");
        Long themeId = createTheme("우주 탈출");
        String date = LocalDate.now().plusDays(1).toString();

        // when
        createUserReservation("브라운", date, firstTimeId, themeId);

        // then
        RestAssured.given().log().all()
                .queryParam("date", date)
                .queryParam("themeId", themeId)
                .when().get("/reservations/available-times")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].id", is(secondTimeId.intValue()));
    }

    @Test
    @DisplayName("같은 날짜 시간 테마의 중복 예약을 거부한다")
    void createReservation_fail_with_duplicated_schedule() {
        // given
        Long timeId = createTime("10:00");
        Long themeId = createTheme("해저 미션");
        String date = LocalDate.now().plusDays(1).toString();
        createUserReservation("브라운", date, timeId, themeId);

        // when & then
        Map<String, Object> params = reservationParams("포비", date, timeId, themeId);
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/users/reservations")
                .then().log().all()
                .statusCode(400)
                .body("code", is("RESERVATION_DUPLICATED"));
    }

    @Test
    @DisplayName("내 예약을 변경하고 취소한다")
    void changeAndDeleteMyReservation_success() {
        // given
        Long firstTimeId = createTime("10:00");
        Long secondTimeId = createTime("11:00");
        Long themeId = createTheme("숲속의 방");
        String date = LocalDate.now().plusDays(1).toString();
        Long reservationId = createUserReservation("브라운", date, firstTimeId, themeId);
        Map<String, Object> changeParams = new HashMap<>();
        changeParams.put("name", "브라운");
        changeParams.put("date", LocalDate.now().plusDays(2).toString());
        changeParams.put("timeId", secondTimeId);

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(changeParams)
                .when().patch("/users/reservations/" + reservationId)
                .then().log().all()
                .statusCode(200)
                .body("time.id", is(secondTimeId.intValue()));

        RestAssured.given().log().all()
                .queryParam("name", "브라운")
                .when().delete("/users/reservations/" + reservationId)
                .then().log().all()
                .statusCode(200);

        RestAssured.given().log().all()
                .queryParam("name", "브라운")
                .when().get("/users/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    @DisplayName("예약이 있는 시간은 삭제할 수 없다")
    void deleteTime_fail_with_reserved_time() {
        // given
        Long timeId = createTime("10:00");
        Long themeId = createTheme("정글 사원");
        String date = LocalDate.now().plusDays(1).toString();
        createUserReservation("브라운", date, timeId, themeId);

        // when & then
        RestAssured.given().log().all()
                .when().delete("/times/" + timeId)
                .then().log().all()
                .statusCode(400)
                .body("code", is("TIME_RESERVED"));
    }

    @Test
    @DisplayName("잘못된 입력은 500이 아닌 사용자 메시지로 응답한다")
    void createReservation_fail_with_invalid_name() {
        // given
        Long timeId = createTime("10:00");
        Long themeId = createTheme("달빛 저택");
        String date = LocalDate.now().plusDays(1).toString();
        Map<String, Object> params = reservationParams("", date, timeId, themeId);

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/users/reservations")
                .then().log().all()
                .statusCode(400)
                .body("code", is("RESERVATION_INVALID_NAME"));
    }

    private Long createTime(String startAt) {
        Map<String, String> params = new HashMap<>();
        params.put("startAt", startAt);
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/times")
                .then().log().all()
                .statusCode(200).extract()
                .jsonPath().getLong("id");
    }

    private Long createTheme(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("description", name + " 설명");
        params.put("thumbnail", "https://example.com/" + name + ".png");
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/themes")
                .then().log().all()
                .statusCode(200).extract()
                .jsonPath().getLong("id");
    }

    private Long createUserReservation(
            String name,
            String date,
            Long timeId,
            Long themeId
    ) {
        Map<String, Object> params = reservationParams(name, date, timeId, themeId);
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/users/reservations")
                .then().log().all()
                .statusCode(200).extract()
                .jsonPath().getLong("id");
    }

    private Map<String, Object> reservationParams(
            String name,
            String date,
            Long timeId,
            Long themeId
    ) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("date", date);
        params.put("timeId", timeId);
        params.put("themeId", themeId);
        return params;
    }
}
