package roomescape.time.domain;

import java.util.List;
import java.util.Optional;

public interface ReservationTimeRepository {

    ReservationTime save(ReservationTime reservationTime);

    ReservationTime saveWithIdIfAbsent(Long id);

    List<ReservationTime> findAll();

    Optional<ReservationTime> findById(Long id);

    Optional<ReservationTime> findByStartAt(String startAt);

    void deleteById(Long id);
}
