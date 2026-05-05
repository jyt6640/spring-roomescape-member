package roomescape.reservations.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.ErrorCode;
import roomescape.global.exception.customException.ReservationException;
import roomescape.global.exception.customException.ReservationTimeException;
import roomescape.reservations.application.dto.ReservationCreateCommand;
import roomescape.reservations.application.dto.ReservationResult;
import roomescape.reservations.domain.Reservation;
import roomescape.reservations.domain.ReservationTime;
import roomescape.reservations.domain.ReservationRepository;
import roomescape.reservations.domain.ReservationTimeRepository;


@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            ReservationTimeRepository reservationTimeRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
    }

    @Transactional
    public ReservationResult saveReservation(ReservationCreateCommand reservationCreate) {
        ReservationTime time = reservationTimeRepository.findById(reservationCreate.timeId())
                .orElseThrow(() -> new ReservationTimeException(ErrorCode.RESERVATION_TIME_NOT_FOUND));
        Reservation reservation = Reservation.createWithNullId(
                reservationCreate.name(),
                reservationCreate.date(),
                time
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
            throw new ReservationException(ErrorCode.RESERVATION_ID_NULL);
        }
        reservationRepository.deleteById(id);
    }
}
