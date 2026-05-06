package roomescape.theme;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import roomescape.theme.entity.Theme;
import roomescape.theme.entity.ThemeRepository;
import roomescape.theme.entity.ThemeSearch;

public class FakeThemeRepository implements ThemeRepository {

    private final Map<Long, Theme> store = new HashMap<>();
    private final List<ThemeSearch> themeSearches = new ArrayList<>();
    private Long sequence = 1L;

    @Override
    public Theme save(Theme theme) {
        if (theme.id() == null) {
            Theme saved = Theme.createWithId(
                    sequence++,
                    theme.name(),
                    theme.description(),
                    theme.thumbnail()
            );
            store.put(saved.id(), saved);
            return saved;
        }
        return theme;
    }

    @Override
    public List<Theme> findAll() {
        return store.values().stream().toList();
    }

    public void addThemeSearch(ThemeSearch themeSearch) {
        themeSearches.add(themeSearch);
    }

    @Override
    public List<ThemeSearch> findPopular(LocalDate from, LocalDate to, int limit) {
        return themeSearches.stream()
                .limit(limit)
                .toList();
    }

    @Override
    public Optional<Theme> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }
}
