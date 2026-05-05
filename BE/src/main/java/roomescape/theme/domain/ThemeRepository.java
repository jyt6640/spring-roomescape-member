package roomescape.theme.domain;

import java.util.List;
import java.util.Optional;
import roomescape.reservation.domain.Reservation;

public interface ThemeRepository {
    Theme save(Reservation reservation);
    Optional<Theme> findById(Long id);
    List<Theme> findAll();
    void deleteById(Long id);
}
