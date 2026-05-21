package roomescape.theme;

import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PopularThemeApiTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("최근 1주 동안 예약이 많은 테마 상위 10개를 조회한다")
    void findPopularThemes_success() {
        // given
        Long firstThemeId = insertTheme("인기 테마");
        Long secondThemeId = insertTheme("보통 테마");
        Long timeId = insertTime("10:00");
        LocalDate yesterday = LocalDate.now().minusDays(1);
        insertReservation("브라운", yesterday, timeId, firstThemeId);
        insertReservation("포비", yesterday, timeId, firstThemeId);
        insertReservation("춘식", yesterday, timeId, secondThemeId);

        // when & then
        RestAssured.given().log().all()
                .when().get("/themes/popular")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(2))
                .body("[0].id", is(firstThemeId.intValue()))
                .body("[0].reservationCount", is(2))
                .body("[1].id", is(secondThemeId.intValue()));
    }

    private Long insertTheme(String name) {
        jdbcTemplate.update(
                "INSERT INTO theme(name, description, thumbnail) VALUES (?, ?, ?)",
                name,
                name + " 설명",
                "https://example.com/" + name + ".png"
        );
        return jdbcTemplate.queryForObject("SELECT MAX(id) FROM theme", Long.class);
    }

    private Long insertTime(String startAt) {
        jdbcTemplate.update("INSERT INTO reservation_time(start_at) VALUES (?)", startAt);
        return jdbcTemplate.queryForObject("SELECT MAX(id) FROM reservation_time", Long.class);
    }

    private void insertReservation(
            String name,
            LocalDate date,
            Long timeId,
            Long themeId
    ) {
        jdbcTemplate.update(
                "INSERT INTO reservation(name, date, time, time_id, theme_id) VALUES (?, ?, ?, ?, ?)",
                name,
                date.toString(),
                "10:00",
                timeId,
                themeId
        );
    }
}
