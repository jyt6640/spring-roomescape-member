package roomescape.theme.application.dto;

import roomescape.global.exception.ErrorCode;
import roomescape.global.exception.ThemeException;

public record ThemeSearchCreateCommand(
        String sortBy,
        String from,
        String to,
        int limit
) {
    public static ThemeSearchCreateCommand create(String sortBy, String from, String to, int limit) {
        validateSortByNull(sortBy);
        validateFromNull(from);
        validateToNull(to);
        validateLimitMinus(limit);
        return new ThemeSearchCreateCommand(sortBy, from, to, limit);
    }

    private static void validateSortByNull(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            throw new ThemeException(ErrorCode.THEME_REQUEST_NULL);
        }
    }

    private static void validateFromNull(String from) {
        if (from == null || from.isBlank()) {
            throw new ThemeException(ErrorCode.THEME_REQUEST_NULL);
        }
    }

    private static void validateToNull(String to) {
        if (to == null || to.isBlank()) {
            throw new ThemeException(ErrorCode.THEME_REQUEST_NULL);
        }
    }

    private static void validateLimitMinus(int limit) {
        if (limit < 0) {
            throw new ThemeException(ErrorCode.THEME_REQUEST_NULL);
        }
    }
}
