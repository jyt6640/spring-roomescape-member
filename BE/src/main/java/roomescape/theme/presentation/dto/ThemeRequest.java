package roomescape.theme.presentation.dto;

import roomescape.global.exception.ErrorCode;
import roomescape.global.exception.ThemeException;

public record ThemeRequest(
        String name,
        String description,
        String thumbnail
) {
    public ThemeRequest {
        validateNameNotEmpty(name);
        validateDescriptionNotEmpty(description);
        validateThumbnailNotEmpty(thumbnail);
    }

    private void validateNameNotEmpty(String name) {
        if(name == null || name.isBlank()) {
            throw new ThemeException(ErrorCode.THEME_REQUEST_NULL);
        }
    }

    public void validateDescriptionNotEmpty(String description) {
        if(description == null || description.isBlank()) {
            throw new ThemeException(ErrorCode.THEME_REQUEST_NULL);
        }
    }

    private void validateThumbnailNotEmpty(String thumbnail) {
        if(thumbnail == null || thumbnail.isBlank()) {
            throw new ThemeException(ErrorCode.THEME_REQUEST_NULL);
        }
    }
}
