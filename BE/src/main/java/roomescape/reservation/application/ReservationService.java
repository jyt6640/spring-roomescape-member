package roomescape.reservation.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.ErrorCode;
import roomescape.global.exception.ReservationException;
import roomescape.global.exception.ReservationTimeException;
import roomescape.reservation.application.dto.ReservationCreateCommand;
import roomescape.reservation.application.dto.ReservationResult;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.domain.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;


@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            ReservationTimeRepository reservationTimeRepository,
            ThemeRepository themeRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
    }

    @Transactional
    public ReservationResult saveReservation(ReservationCreateCommand reservationCreate) {
        ReservationTime time = reservationTimeRepository.findById(reservationCreate.timeId())
                .orElseThrow(() -> new ReservationTimeException(ErrorCode.RESERVATION_TIME_NOT_FOUND));
        Theme theme = themeRepository.findById(reservationCreate.themeId())
                .orElseThrow(() -> new ReservationException(ErrorCode.THEME_NOT_FOUND));
        Reservation reservation = Reservation.createWithNullId(
                reservationCreate.name(),
                reservationCreate.date(),
                time,
                theme
        );
        Reservation savedReservation = reservationRepository.save(reservation);
        return ReservationResult.create(savedReservation);
    }

    public List<ReservationResult> getReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream()
                .map(ReservationResult::create)
                .toList();
    }

    public void deleteReservation(Long id) {
        if (id == null) {
            throw new ReservationException(ErrorCode.RESERVATION_NOT_FOUND);
        }
        reservationRepository.deleteById(id);
    }
}
