package roomescape.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.PopularTheme;
import roomescape.domain.Theme;
import roomescape.exception.RoomescapeException;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ThemeRepository;

@Service
public class ThemeService {

    private static final long DEFAULT_THEME_ID = 1L;

    private final ThemeRepository themeRepository;
    private final ReservationRepository reservationRepository;

    public ThemeService(ThemeRepository themeRepository, ReservationRepository reservationRepository) {
        this.themeRepository = themeRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<Theme> findAll() {
        return themeRepository.findAll();
    }

    @Transactional
    public Theme create(String name, String description, String thumbnailUrl) {
        validateNotBlank(name, "테마 이름은 비어 있을 수 없습니다.");
        validateNotBlank(description, "테마 설명은 비어 있을 수 없습니다.");
        validateNotBlank(thumbnailUrl, "테마 썸네일 URL은 비어 있을 수 없습니다.");
        return themeRepository.save(name, description, thumbnailUrl);
    }

    @Transactional
    public void delete(Long id) {
        requireTheme(id);
        if (reservationRepository.existsByThemeId(id)) {
            throw new RoomescapeException(HttpStatus.CONFLICT, "THEME_IN_USE", "예약이 존재하는 테마는 삭제할 수 없습니다.");
        }
        themeRepository.deleteById(id);
    }

    public Theme requireTheme(Long id) {
        if (id == null) {
            throw new RoomescapeException(HttpStatus.BAD_REQUEST, "INVALID_THEME", "테마를 선택해야 합니다.");
        }
        return themeRepository.findById(id)
                .orElseThrow(() -> new RoomescapeException(HttpStatus.NOT_FOUND, "THEME_NOT_FOUND", "존재하지 않는 테마입니다."));
    }

    @Transactional
    public Theme getOrCreateDefaultTheme(Long themeId) {
        if (themeId != null) {
            return requireTheme(themeId);
        }
        return themeRepository.findById(DEFAULT_THEME_ID)
                .orElseGet(() -> themeRepository.saveWithId(
                        DEFAULT_THEME_ID,
                        "기본 테마",
                        "관리자 호환 API를 위한 기본 테마입니다.",
                        "https://example.com/default-theme.png"
                ));
    }

    public List<PopularTheme> findPopularThemes(LocalDate today) {
        LocalDate start = today.minusDays(7);
        LocalDate end = today.minusDays(1);
        return themeRepository.findPopularThemes(start.toString(), end.toString());
    }

    private void validateNotBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new RoomescapeException(HttpStatus.BAD_REQUEST, "INVALID_INPUT", message);
        }
    }
}
