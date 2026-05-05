package roomescape.domain.theme;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;

public class FakeThemeRepository implements ThemeRepository {

    private final Map<Long, Theme> store = new HashMap<>();
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
    public Optional<Theme> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Theme> findAll() {
        return store.values().stream().toList();
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }
}
