package roomescape.theme.infrastructure;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import roomescape.theme.entity.Theme;
import roomescape.theme.entity.ThemeRepository;
import roomescape.theme.entity.ThemeSearch;

@Repository
public class ThemeJdbcTemplateRepository implements ThemeRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public ThemeJdbcTemplateRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("theme")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Theme save(Theme theme) {
        Map<String, Object> params = Map.of(
                "name", theme.name(),
                "description", theme.description(),
                "thumbnail", theme.thumbnail()
        );
        Long id = simpleJdbcInsert.executeAndReturnKey(params).longValue();
        return Theme.createWithId(id, theme.name(), theme.description(), theme.thumbnail());
    }

    @Override
    public List<Theme> findAll() {
        String sql = "SELECT id, name, description, thumbnail FROM theme";
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> Theme.createWithId(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("thumbnail")
                ));
    }

    @Override
    public List<ThemeSearch> findPopular(LocalDate from, LocalDate to, int limit) {
        String sql = """
        SELECT t.id, t.name
        FROM theme t
        JOIN reservation r ON r.theme_id = t.id
        WHERE r.date BETWEEN ? AND ?
        GROUP BY t.id, t.name
        ORDER BY COUNT(r.id) DESC
        LIMIT ?
        """;
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> ThemeSearch.create(
                        rs.getLong("id"),
                        rs.getString("name")
                ),
                from,
                to,
                limit
        );
    }


    @Override
    public Optional<Theme> findById(Long id) {
        String sql = "SELECT id, name, description, thumbnail FROM theme WHERE id = ?";
        List<Theme> themes = jdbcTemplate.query(sql,
                (rs, rowNum) -> Theme.createWithId(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("thumbnail")
                ),
                id
        );
        return themes.stream()
                .findFirst();
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM theme WHERE id = ?", id);
    }
}
