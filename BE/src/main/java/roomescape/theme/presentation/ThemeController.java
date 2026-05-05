package roomescape.theme.presentation;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.theme.application.ThemeService;
import roomescape.theme.application.dto.ThemeCreateCommand;
import roomescape.theme.application.dto.ThemeResult;
import roomescape.theme.presentation.dto.ThemeRequest;
import roomescape.theme.presentation.dto.ThemeResponse;

@RestController
public class ThemeController {

    private final ThemeService service;

    public ThemeController(ThemeService service) {
        this.service = service;
    }

    @PostMapping("/themes")
    public ResponseEntity<ThemeResponse> saveTheme(
            @RequestBody ThemeRequest request
    ) {
        ThemeResult response = service.saveTheme(
                new ThemeCreateCommand(
                        request.name(),
                        request.description(),
                        request.thumbnail()
                )
        );
        return ResponseEntity.created(URI.create("/themes/" + response.id()))
                .body(ThemeResponse.createResponse(response));
    }

    @GetMapping("/themes")
    public ResponseEntity<List<ThemeResponse>> getThemes() {
        List<ThemeResult> response = service.getThemes();
        return ResponseEntity.ok(response.stream()
                .map(ThemeResponse::createResponse)
                .toList()
        );
    }

    @DeleteMapping("/themes/{id}")
    public ResponseEntity<Void> deleteTheme(
            @PathVariable Long id
    ) {
        service.deleteTheme(id);
        return ResponseEntity.noContent().build();
    }
}
