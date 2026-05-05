package roomescape.reservation.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import roomescape.reservation.infrastructure.entity.AvailableReservationTimeEntity;

public interface ReservationRepository {
    Reservation save(Reservation reservation);
    Optional<Reservation> findById(Long id);
    List<Reservation> findAll();
    boolean existsByReservationTimeId(Long id);
    boolean existsByThemeId(Long id);
    List<AvailableReservationTimeEntity> findAvailableAllTime(LocalDate date, Long themeId);
    void deleteById(Long id);
}
