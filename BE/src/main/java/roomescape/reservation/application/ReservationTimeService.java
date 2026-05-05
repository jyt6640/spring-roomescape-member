package roomescape.reservation.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.ErrorCode;
import roomescape.global.exception.ReservationTimeException;
import roomescape.reservation.application.dto.ReservationTimeCreateCommand;
import roomescape.reservation.application.dto.ReservationTimeResult;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.ReservationTimeRepository;

@Service
public class ReservationTimeService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationRepository reservationRepository;

    public ReservationTimeService(
            ReservationTimeRepository reservationTimeRepository, ReservationRepository reservationRepository
    ) {
        this.reservationTimeRepository = reservationTimeRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public ReservationTimeResult saveTime(ReservationTimeCreateCommand request) {
        ReservationTime reservationTime = ReservationTime.createWithNullId(
                request.startAt()
        );
        ReservationTime savedReservationTime = reservationTimeRepository.save(reservationTime);
        return ReservationTimeResult.createWithId(savedReservationTime);
    }

    public List<ReservationTimeResult> getTimes() {
        List<ReservationTime> times = reservationTimeRepository.findAll();
        return times.stream()
                .map(ReservationTimeResult::createWithId)
                .toList();
    }

    @Transactional
    public void deleteTime(Long id) {
        if (id == null) {
            throw new ReservationTimeException(ErrorCode.RESERVATION_TIME_NOT_FOUND);
        }
        if (reservationRepository.existsByReservationTimeId(id)) {
            throw new ReservationTimeException(ErrorCode.RESERVATION_TIME_ALREADY_USED);
        }
        reservationTimeRepository.deleteById(id);
    }
}
