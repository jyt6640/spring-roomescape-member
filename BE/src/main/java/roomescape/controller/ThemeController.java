package roomescape.controller;

import java.time.LocalDate;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.domain.PopularTheme;
import roomescape.domain.Theme;
import roomescape.dto.ThemeCreateRequest;
import roomescape.service.ThemeService;

@RestController
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @GetMapping("/themes")
    public List<Theme> findThemes() {
        return themeService.findAll();
    }

    @PostMapping("/themes")
    public Theme createTheme(@RequestBody ThemeCreateRequest request) {
        return themeService.create(request.getName(), request.getDescription(), request.getThumbnailUrl());
    }

    @DeleteMapping("/themes/{id}")
    public void deleteTheme(@PathVariable Long id) {
        themeService.delete(id);
    }

    @GetMapping("/api/themes/popular")
    public List<PopularTheme> findPopularThemes() {
        return themeService.findPopularThemes(LocalDate.now());
    }
}
