package roomescape.service;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.ReservationTime;
import roomescape.exception.RoomescapeException;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;

@Service
public class ReservationTimeService {

    private final ReservationTimeRepository timeRepository;
    private final ReservationRepository reservationRepository;

    public ReservationTimeService(ReservationTimeRepository timeRepository, ReservationRepository reservationRepository) {
        this.timeRepository = timeRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<ReservationTime> findAll() {
        return timeRepository.findAll();
    }

    @Transactional
    public ReservationTime create(String startAt) {
        validateNotBlank(startAt, "예약 시간은 비어 있을 수 없습니다.");
        validateTimeFormat(startAt);
        return timeRepository.save(startAt);
    }

    @Transactional
    public void delete(Long id) {
        requireTime(id);
        if (reservationRepository.existsByTimeId(id)) {
            throw new RoomescapeException(HttpStatus.CONFLICT, "TIME_IN_USE", "예약이 존재하는 시간은 삭제할 수 없습니다.");
        }
        timeRepository.deleteById(id);
    }

    public ReservationTime requireTime(Long id) {
        if (id == null) {
            throw new RoomescapeException(HttpStatus.BAD_REQUEST, "INVALID_TIME", "예약 시간을 선택해야 합니다.");
        }
        return timeRepository.findById(id)
                .orElseThrow(() -> new RoomescapeException(HttpStatus.NOT_FOUND, "TIME_NOT_FOUND", "존재하지 않는 예약 시간입니다."));
    }

    @Transactional
    public ReservationTime getOrCreateLegacyTime(String time, Long timeId) {
        if (timeId != null) {
            return timeRepository.findById(timeId)
                    .orElseGet(() -> timeRepository.saveWithId(timeId, "10:00"));
        }
        validateNotBlank(time, "예약 시간은 비어 있을 수 없습니다.");
        validateTimeFormat(time);
        return timeRepository.findByStartAt(time).orElseGet(() -> timeRepository.save(time));
    }

    private void validateTimeFormat(String startAt) {
        if (!startAt.matches("^([01]\\d|2[0-3]):[0-5]\\d$")) {
            throw new RoomescapeException(HttpStatus.BAD_REQUEST, "INVALID_TIME", "예약 시간은 HH:mm 형식이어야 합니다.");
        }
    }

    private void validateNotBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new RoomescapeException(HttpStatus.BAD_REQUEST, "INVALID_INPUT", message);
        }
    }
}
