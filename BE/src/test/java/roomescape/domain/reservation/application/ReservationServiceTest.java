package roomescape.domain.reservation.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.domain.theme.FakeThemeRepository;
import roomescape.global.exception.ReservationTimeException;
import roomescape.domain.reservation.FakeReservationRepository;
import roomescape.domain.reservation.FakeReservationTimeRepository;
import roomescape.reservation.application.ReservationService;
import roomescape.reservation.application.dto.ReservationAvailableCreateCommand;
import roomescape.reservation.application.dto.ReservationAvailableResult;
import roomescape.reservation.application.dto.ReservationCreateCommand;
import roomescape.reservation.application.dto.ReservationResult;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;

class ReservationServiceTest {

    private static final LocalDate TODAY = LocalDate.now();

    private ReservationRepository reservationRepository;
    private ReservationTimeRepository reservationTimeRepository;
    private ThemeRepository themeRepository;
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        reservationTimeRepository = new FakeReservationTimeRepository();
        reservationRepository = new FakeReservationRepository(reservationTimeRepository);
        themeRepository = new FakeThemeRepository();
        reservationService = new ReservationService(reservationRepository, reservationTimeRepository, themeRepository);
    }

    private ReservationTime createReservationTime() {
        return reservationTimeRepository.save(
                ReservationTime.createWithNullId(LocalTime.of(10, 0))
        );
    }

    private ReservationTime createReservationTime(LocalTime startAt) {
        return reservationTimeRepository.save(
                ReservationTime.createWithNullId(startAt)
        );
    }

    private Theme createTheme() {
        return themeRepository.save(
                Theme.createWithNullId("공포", "무서움", "/images/theme/1.jpg")
        );
    }

    private ReservationCreateCommand createReservationCommand(ReservationTime time, Theme theme) {
        return new ReservationCreateCommand(
                "브라운",
                TODAY,
                time.id(),
                theme.id()
        );
    }

    private ReservationResult saveReservation(String name, LocalDate date, ReservationTime time, Theme theme) {
        ReservationCreateCommand command = new ReservationCreateCommand(name, date, time.id(), theme.id());
        return reservationService.saveReservation(command);
    }

    @Test
    @DisplayName("예약을 저장한다")
    void saveReservation() {
        // given
        ReservationTime time = createReservationTime();
        Theme theme = createTheme();

        ReservationCreateCommand command = createReservationCommand(time, theme);

        // when
        ReservationResult savedReservation = reservationService.saveReservation(command);

        // then
        assertThat(savedReservation.time().id()).isEqualTo(time.id());
    }

    @Test
    @DisplayName("존재하지 않는 예약 시간으로 예약하면 예외가 발생한다")
    void saveReservationWithNotFoundTime() {
        // given
        ReservationCreateCommand command = new ReservationCreateCommand(
                "브라운",
                TODAY,
                999L,
                1L
        );

        // when & then
        assertThatThrownBy(() -> reservationService.saveReservation(command))
                .isInstanceOf(ReservationTimeException.class);
    }

    @Test
    @DisplayName("예약 목록을 조회한다")
    void getReservations() {
        // given
        ReservationTime time = createReservationTime();
        Theme theme = createTheme();
        ReservationResult savedReservation = saveReservation(
                "브라운",
                TODAY,
                time,
                theme
        );

        // when
        List<ReservationResult> reservations = reservationService.getReservations();

        // then
        assertThat(reservations).hasSize(1);
        assertThat(reservations.get(0).id()).isEqualTo(savedReservation.id());
    }

    @Test
    @DisplayName("예약이 없으면 빈 목록을 조회한다")
    void getReservationsWhenEmpty() {
        // given & when
        List<ReservationResult> reservations = reservationService.getReservations();

        // then
        assertThat(reservations).isEmpty();
    }

    @Test
    @DisplayName("예약을 삭제한다")
    void deleteReservation() {
        // given
        ReservationTime time = createReservationTime();
        Theme theme = createTheme();
        ReservationResult savedReservation = saveReservation(
                "브라운",
                TODAY,
                time,
                theme
        );

        // when
        reservationService.deleteReservation(savedReservation.id());

        // then
        assertThat(reservationService.getReservations()).isEmpty();
    }

    @Test
    @DisplayName("예약을 할 수 있는 시간을 모두 불러온다")
    void getAllAvailableTimes() {
        // given
        for (int i = 10; i < 15; i++) {
            createReservationTime(LocalTime.of(i, 0));
        }
        createTheme();
        reservationService.saveReservation(
                new ReservationCreateCommand(
                        "홍길동",
                        LocalDate.now(),
                        2L,
                        1L
                )
        );

        // when
        List<ReservationAvailableResult> result =
                reservationService.getAvailableTime(
                        ReservationAvailableCreateCommand.create(
                            LocalDate.now().toString(),
                            1L
                        )
                );

        // then
        assertThat(result).hasSize(5);
        assertThat(result.get(1).available()).isFalse();
    }
}
