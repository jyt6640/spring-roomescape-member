package roomescape.theme.domain;

import java.util.List;
import java.util.Optional;

public interface ThemeRepository {
    Theme save(Theme theme);
    List<Theme> findAll();
    void deleteById(Long id);
}
