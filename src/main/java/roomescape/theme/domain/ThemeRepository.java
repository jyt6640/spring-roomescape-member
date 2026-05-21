package roomescape.theme.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ThemeRepository {

    Theme save(Theme theme);

    List<Theme> findAll();

    Optional<Theme> findById(Long id);

    Optional<Theme> findByName(String name);

    List<PopularTheme> findPopularThemes(
            LocalDate startDate,
            LocalDate endDate,
            int limit
    );

    void deleteById(Long id);
}
