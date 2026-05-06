package roomescape.theme.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ThemeRepository {
    Theme save(Theme theme);
    List<Theme> findAll();
    List<ThemeSearch> findPopular(LocalDate from, LocalDate to, int limit);
    Optional<Theme> findById(Long id);
    void deleteById(Long id);
}
