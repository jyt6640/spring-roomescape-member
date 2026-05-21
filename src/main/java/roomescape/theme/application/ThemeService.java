package roomescape.theme.application;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.EntityNotFoundException;
import roomescape.theme.domain.PopularTheme;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeErrorCode;
import roomescape.theme.domain.ThemeRepository;

@Service
@Transactional
public class ThemeService {

    private static final int POPULAR_THEME_LIMIT = 10;

    private final ThemeRepository themeRepository;

    public ThemeService(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    public Theme create(
            String name,
            String description,
            String thumbnail
    ) {
        Theme theme = Theme.create(name, description, thumbnail);
        return themeRepository.save(theme);
    }

    @Transactional(readOnly = true)
    public List<Theme> findAll() {
        return themeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Theme getById(Long id) {
        return themeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ThemeErrorCode.NOT_FOUND));
    }

    public Theme getOrCreateDefaultTheme() {
        return themeRepository.findByName("기본 테마")
                .orElseGet(() -> themeRepository.save(Theme.defaultTheme()));
    }

    @Transactional(readOnly = true)
    public List<PopularTheme> findPopularThemes(LocalDate today) {
        LocalDate startDate = today.minusDays(7);
        LocalDate endDate = today.minusDays(1);
        return themeRepository.findPopularThemes(startDate, endDate, POPULAR_THEME_LIMIT);
    }

    public void delete(Long id) {
        themeRepository.deleteById(id);
    }
}
