package roomescape.reservation.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import roomescape.reservation.domain.AvailableReservation;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.theme.domain.Theme;
import roomescape.theme.infrastructure.ThemeJdbcTemplateRepository;

class ReservationJdbcTemplateRepositoryTest {

    private ReservationJdbcTemplateRepository reservationRepository;
    private ReservationTimeJdbcTemplateRepository reservationTimeRepository;
    private ThemeJdbcTemplateRepository themeRepository;

    @BeforeEach
    void setUp() {
        DataSource dataSource = createDataSource();
        initializeDatabase(dataSource);
        reservationRepository = new ReservationJdbcTemplateRepository(dataSource);
        reservationTimeRepository = new ReservationTimeJdbcTemplateRepository(dataSource);
        themeRepository = new ThemeJdbcTemplateRepository(dataSource);
    }

    private DataSource createDataSource() {
        String databaseName = "reservation-repository-test-" + System.nanoTime();
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

    private ReservationTime createReservationTime(LocalTime startAt) {
        return reservationTimeRepository.save(ReservationTime.createWithNullId(startAt));
    }

    private Theme createTheme(String name) {
        return themeRepository.save(Theme.createWithNullId(
                name,
                name + " 설명",
                "/images/theme/" + name + ".jpg"
        ));
    }

    private Reservation createReservation(String name, LocalDate date, ReservationTime time, Theme theme) {
        return reservationRepository.save(Reservation.createWithNullId(name, date, time, theme));
    }

    @Test
    @DisplayName("예약을 저장한다")
    void save() {
        // given
        ReservationTime time = createReservationTime(LocalTime.of(10, 0));
        Theme theme = createTheme("공포");
        Reservation reservation = Reservation.createWithNullId(
                "흑곰",
                LocalDate.now(),
                time,
                theme
        );

        // when
        Reservation savedReservation = reservationRepository.save(reservation);

        // then
        assertThat(savedReservation.id()).isNotNull();
        assertThat(savedReservation.name()).isEqualTo("흑곰");
        assertThat(savedReservation.date()).isEqualTo(LocalDate.now());
        assertThat(savedReservation.time()).isEqualTo(time);
        assertThat(savedReservation.theme()).isEqualTo(theme);
    }

    @Test
    @DisplayName("id로 예약을 조회한다")
    void findById() {
        // given
        Reservation savedReservation = createReservation(
                "흑곰",
                LocalDate.now(),
                createReservationTime(LocalTime.of(10, 0)),
                createTheme("공포")
        );

        // when
        Reservation foundReservation = reservationRepository.findById(savedReservation.id()).orElseThrow();

        // then
        assertThat(foundReservation.id()).isEqualTo(savedReservation.id());
        assertThat(foundReservation.name()).isEqualTo("흑곰");
        assertThat(foundReservation.date()).isEqualTo(LocalDate.now());
        assertThat(foundReservation.time()).isEqualTo(savedReservation.time());
        assertThat(foundReservation.theme().id()).isEqualTo(savedReservation.theme().id());
    }

    @Test
    @DisplayName("예약 전체 목록을 조회한다")
    void findAll() {
        // given
        Theme theme = createTheme("공포");
        ReservationTime firstTime = createReservationTime(LocalTime.of(10, 0));
        ReservationTime secondTime = createReservationTime(LocalTime.of(11, 0));
        Reservation firstReservation = createReservation(
                "흑곰",
                LocalDate.now(),
                firstTime,
                theme
        );
        Reservation secondReservation = createReservation(
                "라티",
                LocalDate.now().plusDays(1),
                secondTime,
                theme
        );

        // when
        List<Reservation> reservations = reservationRepository.findAll();

        // then
        assertThat(reservations).hasSize(2);
        assertThat(reservations.get(0).id()).isEqualTo(firstReservation.id());
        assertThat(reservations.get(1).id()).isEqualTo(secondReservation.id());
    }

    @Test
    @DisplayName("예약 시간 id가 예약에서 사용 중인지 확인한다")
    void existsByReservationTimeId() {
        // given
        ReservationTime usedTime = createReservationTime(LocalTime.of(10, 0));
        ReservationTime unusedTime = createReservationTime(LocalTime.of(11, 0));
        createReservation("흑곰", LocalDate.now(), usedTime, createTheme("공포"));

        // when & then
        assertThat(reservationRepository.existsByReservationTimeId(usedTime.id())).isTrue();
        assertThat(reservationRepository.existsByReservationTimeId(unusedTime.id())).isFalse();
    }

    @Test
    @DisplayName("테마 id가 예약에서 사용 중인지 확인한다")
    void existsByThemeId() {
        // given
        Theme usedTheme = createTheme("공포");
        Theme unusedTheme = createTheme("추리");
        createReservation(
                "흑곰",
                LocalDate.now(),
                createReservationTime(LocalTime.of(10, 0)),
                usedTheme
        );

        // when & then
        assertThat(reservationRepository.existsByThemeId(usedTheme.id())).isTrue();
        assertThat(reservationRepository.existsByThemeId(unusedTheme.id())).isFalse();
    }

    @Test
    @DisplayName("날짜와 테마 기준으로 예약 가능한 시간을 조회한다")
    void findAvailableAllTime() {
        // given
        ReservationTime firstTime = createReservationTime(LocalTime.of(10, 0));
        ReservationTime secondTime = createReservationTime(LocalTime.of(11, 0));
        ReservationTime thirdTime = createReservationTime(LocalTime.of(12, 0));
        Theme horrorTheme = createTheme("공포");
        Theme mysteryTheme = createTheme("추리");

        LocalDate date = LocalDate.now();
        createReservation("흑곰", date, secondTime, horrorTheme);
        createReservation("라티", date, thirdTime, mysteryTheme);

        // when
        List<AvailableReservation> availableTimes = reservationRepository.findAvailableAllTime(
                date,
                horrorTheme.id()
        );

        // then
        assertThat(availableTimes).contains(
                new AvailableReservation(date, firstTime.id(), horrorTheme.id(), true),
                new AvailableReservation(date, secondTime.id(), horrorTheme.id(), false),
                new AvailableReservation(date, thirdTime.id(), horrorTheme.id(), true)
        );
    }

    @Test
    @DisplayName("예약을 삭제한다")
    void deleteById() {
        // given
        Reservation savedReservation = createReservation(
                "흑곰",
                LocalDate.now(),
                createReservationTime(LocalTime.of(10, 0)),
                createTheme("공포")
        );

        // when
        reservationRepository.deleteById(savedReservation.id());

        // then
        assertThat(reservationRepository.findById(savedReservation.id())).isEmpty();
    }
}
