package roomescape.theme.entity;

public enum ThemeSortType {
    POPULAR;

    public static ThemeSortType from(String value) {
        return ThemeSortType.valueOf(value.toUpperCase());
    }
}
