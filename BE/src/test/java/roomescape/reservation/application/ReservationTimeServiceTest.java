package roomescape.reservation.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.reservation.FakeReservationRepository;
import roomescape.reservation.FakeReservationTimeRepository;
import roomescape.theme.FakeThemeRepository;
import roomescape.reservation.application.dto.ReservationCreateCommand;
import roomescape.reservation.application.dto.ReservationTimeCreateCommand;
import roomescape.reservation.application.dto.ReservationTimeResult;
import roomescape.reservation.entity.ReservationRepository;
import roomescape.reservation.entity.ReservationTimeRepository;
import roomescape.theme.application.ThemeService;
import roomescape.theme.application.dto.ThemeCreateCommand;
import roomescape.theme.application.dto.ThemeResult;
import roomescape.theme.entity.ThemeRepository;

class ReservationTimeServiceTest {

    private static final LocalDate TODAY = LocalDate.now();

    private ReservationRepository reservationRepository;
    private ReservationService reservationService;
    private ReservationTimeRepository reservationTimeRepository;
    private ReservationTimeService reservationTimeService;
    private ThemeRepository themeRepository;
    private ThemeService themeService;


    @BeforeEach
    void setUp() {
        reservationTimeRepository = new FakeReservationTimeRepository();
        reservationRepository = new FakeReservationRepository(reservationTimeRepository);
        themeRepository = new FakeThemeRepository();
        themeService = new ThemeService(themeRepository);
        reservationService = new ReservationService(reservationRepository, reservationTimeRepository, themeRepository);
        reservationTimeService = new ReservationTimeService(reservationTimeRepository, reservationRepository);
    }

    private ReservationCreateCommand createReservationCommand(ReservationTimeResult time, ThemeResult theme) {
        return new ReservationCreateCommand(
                "브라운",
                TODAY,
                time.id(),
                theme.id()
        );
    }

    private ReservationTimeCreateCommand createReservationTimeCommand() {
        return ReservationTimeCreateCommand.create(LocalTime.of(10, 0));
    }

    private ThemeCreateCommand createThemeCommand() {
        return ThemeCreateCommand.create("공포", "무서움", "/images/theme/1.jpg");
    }

    private ReservationTimeResult saveTime(LocalTime startAt) {
        return reservationTimeService.saveTime(ReservationTimeCreateCommand.create(startAt));
    }

    private ThemeResult saveTheme(String name, String description, String thumbnail) {
        return themeService.saveTheme(ThemeCreateCommand.create(name, description, thumbnail));
    }

    @Test
    @DisplayName("예약 시간을 저장한다")
    void saveTime() {
        // given
        ReservationTimeCreateCommand command = createReservationTimeCommand();

        // when
        ReservationTimeResult savedTime = reservationTimeService.saveTime(command);

        // then
        assertThat(savedTime.id()).isNotNull();
    }

    @Test
    @DisplayName("예약 시간 목록을 조회한다")
    void getTimes() {
        // given
        ReservationTimeResult firstTime = saveTime(LocalTime.of(10, 0));
        ReservationTimeResult secondTime = saveTime(LocalTime.of(11, 0));

        // when
        List<ReservationTimeResult> times = reservationTimeService.getTimes();

        // then
        assertThat(times).hasSize(2);
        assertThat(times).containsExactlyInAnyOrder(firstTime, secondTime);
    }

    @Test
    @DisplayName("예약 시간이 없으면 빈 목록을 조회한다")
    void getTimesWhenEmpty() {
        // when
        List<ReservationTimeResult> times = reservationTimeService.getTimes();

        // then
        assertThat(times).isEmpty();
    }

    @Test
    @DisplayName("예약 시간을 삭제한다")
    void deleteTime() {
        // given
        ReservationTimeResult savedTime = saveTime(LocalTime.of(10, 0));

        // when
        reservationTimeService.deleteTime(savedTime.id());

        // then
        assertThat(reservationTimeService.getTimes()).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 예약 시간을 삭제해도 예외가 발생하지 않는다")
    void deleteNotFoundTime() {
        // when
        reservationTimeService.deleteTime(999L);

        // then
        assertThat(reservationTimeService.getTimes()).isEmpty();
    }

    @Test
    @DisplayName("예약 시간 id가 예약에서 참조되고 있는지 확인 기능")
    void existsByReservationTimeId() {
        // given
        ReservationTimeResult savedTime = saveTime(LocalTime.of(10, 0));
        ThemeResult themeResult = saveTheme("공포", "무서움", "/images/theme/1.jpg");
        ReservationCreateCommand command = createReservationCommand(savedTime, themeResult);
        reservationService.saveReservation(command);

        // when
        boolean exists = reservationRepository.existsByReservationTimeId(savedTime.id());

        // then
        assertThat(exists).isTrue();
    }
}
