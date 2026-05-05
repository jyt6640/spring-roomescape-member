package roomescape.domain.reservations.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.global.exception.customException.ReservationTimeException;
import roomescape.domain.reservations.FakeReservationRepository;
import roomescape.domain.reservations.FakeReservationTimeRepository;
import roomescape.reservations.application.ReservationService;
import roomescape.reservations.application.dto.ReservationCreateCommand;
import roomescape.reservations.application.dto.ReservationResult;
import roomescape.reservations.domain.ReservationRepository;
import roomescape.reservations.domain.ReservationTime;
import roomescape.reservations.domain.ReservationTimeRepository;

class ReservationServiceTest {

    private static final LocalDate TODAY = LocalDate.now();

    private ReservationRepository reservationRepository;
    private ReservationTimeRepository reservationTimeRepository;
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        reservationRepository = new FakeReservationRepository();
        reservationTimeRepository = new FakeReservationTimeRepository();
        reservationService = new ReservationService(reservationRepository, reservationTimeRepository);
    }

    private ReservationTime createReservationTime() {
        return reservationTimeRepository.save(
                ReservationTime.createWithNullId(LocalTime.of(10, 0))
        );
    }

    private ReservationCreateCommand createReservationCommand(ReservationTime time) {
        return new ReservationCreateCommand(
                "브라운",
                TODAY,
                time.id()
        );
    }

    private ReservationResult saveReservation(String name, LocalDate date, ReservationTime time) {
        ReservationCreateCommand command = new ReservationCreateCommand(name, date, time.id());
        return reservationService.saveReservation(command);
    }

    @Test
    @DisplayName("예약을 저장한다")
    void saveReservation() {
        // given
        ReservationTime time = createReservationTime();
        ReservationCreateCommand command = createReservationCommand(time);

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
                999L
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
        ReservationResult savedReservation = saveReservation(
                "브라운",
                TODAY,
                time
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
        ReservationResult savedReservation = saveReservation(
                "브라운",
                TODAY,
                time
        );

        // when
        reservationService.deleteReservation(savedReservation.id());

        // then
        assertThat(reservationService.getReservations()).isEmpty();
    }
}
