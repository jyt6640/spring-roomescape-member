package roomescape.reservation.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository {

    Reservation save(Reservation reservation);

    List<Reservation> findAll();

    List<Reservation> findByName(String name);

    Optional<Reservation> findById(Long id);

    boolean existsByDateAndTimeIdAndThemeId(
            LocalDate date,
            Long timeId,
            Long themeId
    );

    boolean existsByDateAndTimeIdAndThemeIdExceptId(
            LocalDate date,
            Long timeId,
            Long themeId,
            Long id
    );

    boolean existsByTimeId(Long timeId);

    List<Long> findReservedTimeIds(
            LocalDate date,
            Long themeId
    );

    void updateSchedule(Reservation reservation);

    void deleteById(Long id);
}
