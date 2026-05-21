package roomescape;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.controller.ReservationController;
import roomescape.domain.Reservation;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReservationAcceptanceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ReservationController reservationController;

    @Test
    void 예약_조회() {
        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    void 예약_추가_및_삭제() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "브라운");
        params.put("date", "2023-08-05");
        params.put("time", "15:40");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("id", is(1));

        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));

        RestAssured.given().log().all()
                .when().delete("/reservations/1")
                .then().log().all()
                .statusCode(200);

        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    void 데이터베이스_연동() {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            assertThat(connection).isNotNull();
            assertThat(connection.getCatalog()).isEqualTo("DATABASE");
            assertThat(connection.getMetaData().getTables(null, null, "RESERVATION", null).next()).isTrue();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void 시간_관리_API() {
        Map<String, String> params = new HashMap<>();
        params.put("startAt", "10:00");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/times")
                .then().log().all()
                .statusCode(200);

        RestAssured.given().log().all()
                .when().get("/times")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));

        RestAssured.given().log().all()
                .when().delete("/times/1")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void 예약과_시간_연결() {
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("name", "브라운");
        reservation.put("date", "2023-08-05");
        reservation.put("timeId", 1);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(200);

        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));
    }

    @Test
    void 계층화_리팩터링() {
        boolean isJdbcTemplateInjected = false;

        for (Field field : reservationController.getClass().getDeclaredFields()) {
            if (field.getType().equals(JdbcTemplate.class)) {
                isJdbcTemplateInjected = true;
                break;
            }
        }

        assertThat(isJdbcTemplateInjected).isFalse();
    }

    @Test
    void 정상_흐름_예약_가능_시간_조회_예약_생성_다시_조회시_빠짐() {
        Long themeId = createTheme();
        Long firstTimeId = createTime("10:00");
        createTime("11:00");
        String date = LocalDate.now().plusDays(1).toString();

        RestAssured.given().log().all()
                .when().get("/api/reservation-times/available?date={date}&themeId={themeId}", date, themeId)
                .then().log().all()
                .statusCode(200)
                .body("size()", is(2));

        Map<String, Object> reservation = new HashMap<>();
        reservation.put("name", "브라운");
        reservation.put("date", date);
        reservation.put("timeId", firstTimeId);
        reservation.put("themeId", themeId);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(reservation)
                .when().post("/api/reservations")
                .then().log().all()
                .statusCode(200);

        RestAssured.given().log().all()
                .when().get("/api/reservation-times/available?date={date}&themeId={themeId}", date, themeId)
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));
    }

    @Test
    void 내_예약_조회_변경_취소() {
        Long themeId = createTheme();
        Long firstTimeId = createTime("10:00");
        Long secondTimeId = createTime("11:00");
        String date = LocalDate.now().plusDays(1).toString();
        String changedDate = LocalDate.now().plusDays(2).toString();
        Long reservationId = createUserReservation("브라운", date, firstTimeId, themeId);

        RestAssured.given().log().all()
                .when().get("/api/reservations?name={name}", "브라운")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));

        Map<String, Object> update = new HashMap<>();
        update.put("date", changedDate);
        update.put("timeId", secondTimeId);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(update)
                .when().patch("/api/reservations/{id}?name={name}", reservationId, "브라운")
                .then().log().all()
                .statusCode(200)
                .body("date", is(changedDate));

        RestAssured.given().log().all()
                .when().delete("/api/reservations/{id}?name={name}", reservationId, "브라운")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void 사용자_예약_정책_위반은_의도된_에러를_반환한다() {
        Long themeId = createTheme();
        Long timeId = createTime("10:00");

        Map<String, Object> reservation = new HashMap<>();
        reservation.put("name", "");
        reservation.put("date", LocalDate.now().plusDays(1).toString());
        reservation.put("timeId", timeId);
        reservation.put("themeId", themeId);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(reservation)
                .when().post("/api/reservations")
                .then().log().all()
                .statusCode(400)
                .body("code", is("INVALID_INPUT"));
    }

    private Long createTheme() {
        Map<String, String> theme = new HashMap<>();
        theme.put("name", "공포의 방");
        theme.put("description", "어두운 저택에서 단서를 찾는 테마");
        theme.put("thumbnailUrl", "https://example.com/horror.png");

        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(theme)
                .when().post("/themes")
                .then().statusCode(200)
                .extract().jsonPath().getLong("id");
    }

    private Long createTime(String startAt) {
        Map<String, String> time = new HashMap<>();
        time.put("startAt", startAt);

        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(time)
                .when().post("/times")
                .then().statusCode(200)
                .extract().jsonPath().getLong("id");
    }

    private Long createUserReservation(String name, String date, Long timeId, Long themeId) {
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("name", name);
        reservation.put("date", date);
        reservation.put("timeId", timeId);
        reservation.put("themeId", themeId);

        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(reservation)
                .when().post("/api/reservations")
                .then().statusCode(200)
                .extract().jsonPath().getLong("id");
    }
}
