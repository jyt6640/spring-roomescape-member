package roomescape.reservations.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.ErrorCode;
import roomescape.global.exception.customException.ReservationTimeException;
import roomescape.reservations.application.dto.ReservationTimeCreateCommand;
import roomescape.reservations.application.dto.ReservationTimeResult;
import roomescape.reservations.domain.ReservationRepository;
import roomescape.reservations.domain.ReservationTime;
import roomescape.reservations.domain.ReservationTimeRepository;

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
            throw new ReservationTimeException(ErrorCode.RESERVATION_TIME_ID_NULL);
        }
        if (reservationRepository.existsByReservationTimeId(id)) {
            throw new ReservationTimeException(ErrorCode.RESERVATION_TIME_ALREADY_USED);
        }
        reservationTimeRepository.deleteById(id);
    }
}
