package roomescape.theme.application;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.theme.application.dto.ThemeCreateCommand;
import roomescape.theme.application.dto.ThemeResult;
import roomescape.theme.application.dto.ThemeSearchCreateCommand;
import roomescape.theme.application.dto.ThemeSearchResult;
import roomescape.theme.entity.Theme;
import roomescape.theme.entity.ThemeRepository;
import roomescape.theme.entity.ThemeSortType;
import roomescape.theme.entity.ThemeSearch;

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

    public List<ThemeSearchResult> getSearchTheme(ThemeSearchCreateCommand request) {
        ThemeSortType sortType = ThemeSortType.from(request.sortBy());
        if (sortType == ThemeSortType.POPULAR) {
            return getPopularThemes(request);
        }
        return List.of();
    }

    public void deleteTheme(Long id) {
        themeRepository.deleteById(id);
    }

    private List<ThemeSearchResult> getPopularThemes(ThemeSearchCreateCommand request) {
        List<ThemeSearch> popularThemes = themeRepository.findPopular(
                LocalDate.parse(request.from()),
                LocalDate.parse(request.to()),
                request.limit()
        );
        return popularThemes.stream()
                .map(ThemeSearchResult::create)
                .toList();
    }
}
