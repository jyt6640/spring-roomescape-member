package roomescape.domain.theme.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.domain.theme.FakeThemeRepository;
import roomescape.theme.application.ThemeService;
import roomescape.theme.application.dto.ThemeCreateCommand;
import roomescape.theme.application.dto.ThemeResult;
import roomescape.theme.domain.ThemeRepository;

public class ThemeServiceTest {

    private ThemeRepository themeRepository;
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
        List<ThemeResult> result = theme.getThemes();

        // then
        assertThat(result.size()).isEqualTo(0);
    }
}
