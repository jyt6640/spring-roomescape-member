package roomescape.theme.domain;

import java.util.Objects;
import roomescape.global.exception.BusinessException;

public class Theme {

    private static final String DEFAULT_NAME = "기본 테마";
    private static final String DEFAULT_DESCRIPTION = "관리자 예약 기본 테마";
    private static final String DEFAULT_THUMBNAIL = "https://example.com/default-theme.png";

    private final Long id;
    private final String name;
    private final String description;
    private final String thumbnail;

    private Theme(
            Long id,
            String name,
            String description,
            String thumbnail
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    public static Theme create(
            String name,
            String description,
            String thumbnail
    ) {
        validate(name, description, thumbnail);
        return new Theme(null, name, description, thumbnail);
    }

    public static Theme restore(
            Long id,
            String name,
            String description,
            String thumbnail
    ) {
        return new Theme(id, name, description, thumbnail);
    }

    public static Theme defaultTheme() {
        return create(DEFAULT_NAME, DEFAULT_DESCRIPTION, DEFAULT_THUMBNAIL);
    }

    public boolean isDefault() {
        return DEFAULT_NAME.equals(name);
    }

    public Theme persisted(Long id) {
        return new Theme(id, name, description, thumbnail);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    private static void validate(
            String name,
            String description,
            String thumbnail
    ) {
        validateName(name);
        validateDescription(description);
        validateThumbnail(thumbnail);
    }

    private static void validateName(String name) {
        if (isBlank(name)) {
            throw new BusinessException(ThemeErrorCode.INVALID_NAME);
        }
    }

    private static void validateDescription(String description) {
        if (isBlank(description)) {
            throw new BusinessException(ThemeErrorCode.INVALID_DESCRIPTION);
        }
    }

    private static void validateThumbnail(String thumbnail) {
        if (isBlank(thumbnail)) {
            throw new BusinessException(ThemeErrorCode.INVALID_THUMBNAIL);
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Theme other)) {
            return false;
        }
        if (id == null || other.id == null) {
            return false;
        }
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        if (id == null) {
            return System.identityHashCode(this);
        }
        return Objects.hash(id);
    }
}
