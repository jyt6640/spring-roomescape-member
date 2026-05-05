package roomescape.theme.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeSearch;

class ThemeJdbcTemplateRepositoryTest {

    private ThemeJdbcTemplateRepository themeRepository;
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        DataSource dataSource = createDataSource();
        initializeDatabase(dataSource);
        themeRepository = new ThemeJdbcTemplateRepository(dataSource);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private List<Long> createReservationTimes(int count) {
        return java.util.stream.LongStream.rangeClosed(1, count)
                .mapToObj(index -> createReservationTime(LocalTime.of((int) (9 + index), 0)))
                .toList();
    }

    private Long createReservationTime(LocalTime startAt) {
        jdbcTemplate.update(
                "INSERT INTO reservation_time (start_at) VALUES (?)",
                Time.valueOf(startAt)
        );
        return jdbcTemplate.queryForObject(
                "SELECT id FROM reservation_time WHERE start_at = ?",
                Long.class,
                Time.valueOf(startAt)
        );
    }

    private void createReservation(String name, LocalDate date, Long timeId, Long themeId) {
        jdbcTemplate.update(
                "INSERT INTO reservation (name, date, time_id, theme_id) VALUES (?, ?, ?, ?)",
                name,
                date,
                timeId,
                themeId
        );
    }

    private DataSource createDataSource() {
        String databaseName = "theme-repository-test-" + System.nanoTime();
        return new DriverManagerDataSource(
                "jdbc:h2:mem:" + databaseName + ";DB_CLOSE_DELAY=-1",
                "sa",
                ""
        );
    }

    private void initializeDatabase(DataSource dataSource) {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(
                new ClassPathResource("schema.sql")
        );
        databasePopulator.execute(dataSource);
    }

    @Test
    @DisplayName("테마를 저장한다")
    void save() {
        // given
        Theme theme = Theme.createWithNullId("공포", "무서움", "/images/theme/1.jpg");

        // when
        Theme savedTheme = themeRepository.save(theme);

        // then
        assertThat(savedTheme.id()).isNotNull();
        assertThat(savedTheme.name()).isEqualTo("공포");
        assertThat(savedTheme.description()).isEqualTo("무서움");
        assertThat(savedTheme.thumbnail()).isEqualTo("/images/theme/1.jpg");
    }

    @Test
    @DisplayName("테마 전체 목록을 조회한다")
    void findAll() {
        // given
        Theme firstTheme = themeRepository.save(Theme.createWithNullId("공포", "무서움", "/images/theme/1.jpg"));
        Theme secondTheme = themeRepository.save(Theme.createWithNullId("추리", "어려움", "/images/theme/2.jpg"));

        // when
        List<Theme> themes = themeRepository.findAll();

        // then
        assertThat(themes).containsExactly(firstTheme, secondTheme);
    }

    @Test
    @DisplayName("id로 테마를 조회한다")
    void findById() {
        // given
        Theme savedTheme = themeRepository.save(Theme.createWithNullId("공포", "무서움", "/images/theme/1.jpg"));

        // when
        Theme foundTheme = themeRepository.findById(savedTheme.id()).orElseThrow();

        // then
        assertThat(foundTheme).isEqualTo(savedTheme);
    }

    @Test
    @DisplayName("테마를 삭제한다")
    void deleteById() {
        // given
        Theme savedTheme = themeRepository.save(Theme.createWithNullId("공포", "무서움", "/images/theme/1.jpg"));

        // when
        themeRepository.deleteById(savedTheme.id());

        // then
        assertThat(themeRepository.findById(savedTheme.id())).isEmpty();
    }

    @Test
    @DisplayName("인기 테마를 제한 개수만큼 조회한다")
    void findPopular() {
        // given
        Theme mysteryTheme = themeRepository.save(Theme.createWithNullId("추리", "어려움", "/images/theme/1.jpg"));
        Theme horrorTheme = themeRepository.save(Theme.createWithNullId("공포", "무서움", "/images/theme/2.jpg"));
        Theme adventureTheme = themeRepository.save(Theme.createWithNullId("모험", "즐거움", "/images/theme/3.jpg"));
        Theme outsideTheme = themeRepository.save(Theme.createWithNullId("감성", "잔잔함", "/images/theme/4.jpg"));

        List<Long> timeIds = createReservationTimes(8);
        LocalDate now = LocalDate.now();

        createReservation("흑곰", now, timeIds.get(0), mysteryTheme.id());
        createReservation("재키", now, timeIds.get(1), mysteryTheme.id());
        createReservation("로치", now, timeIds.get(2), mysteryTheme.id());
        createReservation("라티", now, timeIds.get(3), horrorTheme.id());
        createReservation("피온", now, timeIds.get(4), horrorTheme.id());
        createReservation("워넬", now, timeIds.get(5), adventureTheme.id());
        createReservation("카키", now, timeIds.get(6), outsideTheme.id());
        createReservation("포비", now, timeIds.get(7), outsideTheme.id());

        // when
        List<ThemeSearch> popularThemes = themeRepository.findPopular(
                LocalDate.now().minusDays(7),
                LocalDate.now(),
                2
        );

        // then
        assertThat(popularThemes).contains(
                ThemeSearch.create(mysteryTheme.id(), "추리"),
                ThemeSearch.create(horrorTheme.id(), "공포")
        );
    }
}
