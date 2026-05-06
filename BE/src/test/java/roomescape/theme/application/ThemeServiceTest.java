package roomescape.theme.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.theme.FakeThemeRepository;
import roomescape.theme.application.dto.ThemeCreateCommand;
import roomescape.theme.application.dto.ThemeResult;
import roomescape.theme.application.dto.ThemeSearchCreateCommand;
import roomescape.theme.application.dto.ThemeSearchResult;
import roomescape.theme.entity.ThemeSearch;

public class ThemeServiceTest {

    private FakeThemeRepository themeRepository;
    private ThemeService themeService;

    @BeforeEach
    void setUp() {
        themeRepository = new FakeThemeRepository();
        themeService = new ThemeService(themeRepository);
    }

    private ThemeCreateCommand createThemeResult() {
        return new ThemeCreateCommand(
                "공포",
                "무서움",
                "/images/theme/1.jpg"
        );
    }

    @Test
    @DisplayName("테마 추가")
    void addTheme() {
        // given
        ThemeCreateCommand theme = createThemeResult();

        // when
        ThemeResult savedTheme = themeService.saveTheme(theme);

        // then
        assertThat(savedTheme.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("테마 전체 조회")
    void findByIdForTheme() {
        // given
        ThemeCreateCommand theme = createThemeResult();
        themeService.saveTheme(theme);
        themeService.saveTheme(theme);

        // when
        List<ThemeResult> result = themeService.getThemes();

        // then
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("테마 삭제")
    void deleteById() {
        // given
        ThemeCreateCommand theme = createThemeResult();
        themeService.saveTheme(theme);

        // when
        themeService.deleteTheme(1L);
        List<ThemeResult> result = themeService.getThemes();

        // then
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("인기 테마를 10개만 조회한다")
    void getPopularThemes() {
        // given
        for (long id = 1L; id <= 12L; id++) {
            themeRepository.addThemeSearch(ThemeSearch.create(id, "테마" + id));
        }
        ThemeSearchCreateCommand command = ThemeSearchCreateCommand.create(
                "popular",
                "2026-05-01",
                "2026-05-07",
                10
        );

        // when
        List<ThemeSearchResult> result = themeService.getSearchTheme(command);

        // then
        assertThat(result).hasSize(10);
        assertThat(result.get(0)).isEqualTo(new ThemeSearchResult(1L, "테마1"));
        assertThat(result.get(9)).isEqualTo(new ThemeSearchResult(10L, "테마10"));
    }
}
