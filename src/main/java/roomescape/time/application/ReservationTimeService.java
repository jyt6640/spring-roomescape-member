package roomescape.time.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.BusinessException;
import roomescape.global.exception.EntityNotFoundException;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.domain.ReservationTimeErrorCode;
import roomescape.time.domain.ReservationTimeRepository;

@Service
@Transactional
public class ReservationTimeService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationRepository reservationRepository;

    public ReservationTimeService(
            ReservationTimeRepository reservationTimeRepository,
            ReservationRepository reservationRepository
    ) {
        this.reservationTimeRepository = reservationTimeRepository;
        this.reservationRepository = reservationRepository;
    }

    public ReservationTime create(String startAt) {
        ReservationTime reservationTime = ReservationTime.create(startAt);
        return reservationTimeRepository.save(reservationTime);
    }

    @Transactional(readOnly = true)
    public List<ReservationTime> findAll() {
        return reservationTimeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ReservationTime getById(Long id) {
        return reservationTimeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ReservationTimeErrorCode.NOT_FOUND));
    }

    public ReservationTime getOrCreateByStartAt(String startAt) {
        return reservationTimeRepository.findByStartAt(startAt)
                .orElseGet(() -> create(startAt));
    }

    public ReservationTime getOrCreateDefault(Long id) {
        return reservationTimeRepository.findById(id)
                .orElseGet(() -> reservationTimeRepository.saveWithIdIfAbsent(id));
    }

    public void delete(Long id) {
        validateUnused(id);
        reservationTimeRepository.deleteById(id);
    }

    private void validateUnused(Long id) {
        if (reservationRepository.existsByTimeId(id)) {
            throw new BusinessException(ReservationTimeErrorCode.RESERVED_TIME);
        }
    }
}
