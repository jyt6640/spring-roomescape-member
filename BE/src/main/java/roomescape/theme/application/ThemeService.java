package roomescape.theme.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.theme.application.dto.ThemeCreateCommand;
import roomescape.theme.application.dto.ThemeResult;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;

@Service
public class ThemeService {

    private final ThemeRepository themeRepository;

    public ThemeService(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    @Transactional
    public ThemeResult saveTheme(ThemeCreateCommand request) {
        Theme theme = Theme.createWithNullId(
                request.name(),
                request.description(),
                request.thumbnail()
        );
        Theme savedTheme = themeRepository.save(theme);
        return ThemeResult.create(savedTheme);
    }

    public List<ThemeResult> getThemes() {
        List<Theme> themes = themeRepository.findAll();
        return themes.stream()
                .map(ThemeResult::create)
                .toList();
    }

    public void deleteTheme(Long id) {
        themeRepository.deleteById(id);
    }
}
