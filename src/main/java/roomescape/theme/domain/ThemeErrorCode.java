package roomescape.theme.domain;

import org.springframework.http.HttpStatus;
import roomescape.global.exception.ErrorCode;

public enum ThemeErrorCode implements ErrorCode {

    INVALID_NAME(HttpStatus.BAD_REQUEST, "THEME_INVALID_NAME", "테마 이름을 입력해 주세요."),
    INVALID_DESCRIPTION(HttpStatus.BAD_REQUEST, "THEME_INVALID_DESCRIPTION", "테마 설명을 입력해 주세요."),
    INVALID_THUMBNAIL(HttpStatus.BAD_REQUEST, "THEME_INVALID_THUMBNAIL", "테마 썸네일 URL을 입력해 주세요."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "THEME_NOT_FOUND", "테마를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ThemeErrorCode(
            HttpStatus status,
            String code,
            String message
    ) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus status() {
        return status;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
