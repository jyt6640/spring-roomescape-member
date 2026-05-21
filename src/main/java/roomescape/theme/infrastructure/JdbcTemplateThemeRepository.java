package roomescape.theme.infrastructure;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.theme.domain.PopularTheme;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;

@Repository
public class JdbcTemplateThemeRepository implements ThemeRepository {

    private static final String INSERT_SQL = """
            INSERT INTO theme(name, description, thumbnail)
            VALUES (?, ?, ?)
            """;
    private static final String SELECT_ALL_SQL = """
            SELECT id, name, description, thumbnail
            FROM theme
            ORDER BY id
            """;
    private static final String SELECT_BY_ID_SQL = """
            SELECT id, name, description, thumbnail
            FROM theme
            WHERE id = ?
            """;
    private static final String SELECT_BY_NAME_SQL = """
            SELECT id, name, description, thumbnail
            FROM theme
            WHERE name = ?
            """;
    private static final String SELECT_POPULAR_SQL = """
            SELECT
                t.id,
                t.name,
                t.description,
                t.thumbnail,
                COUNT(r.id) AS reservation_count
            FROM theme AS t
            INNER JOIN reservation AS r
                ON r.theme_id = t.id
            WHERE r.date BETWEEN ? AND ?
            GROUP BY t.id, t.name, t.description, t.thumbnail
            ORDER BY reservation_count DESC, t.id ASC
            LIMIT ?
            """;
    private static final String DELETE_SQL = """
            DELETE FROM theme
            WHERE id = ?
            """;
    private static final RowMapper<Theme> THEME_ROW_MAPPER = (resultSet, rowNumber) -> Theme.restore(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("description"),
            resultSet.getString("thumbnail")
    );
    private static final RowMapper<PopularTheme> POPULAR_THEME_ROW_MAPPER = (resultSet, rowNumber) -> new PopularTheme(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("description"),
            resultSet.getString("thumbnail"),
            resultSet.getLong("reservation_count")
    );

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateThemeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Theme save(Theme theme) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    INSERT_SQL,
                    Statement.RETURN_GENERATED_KEYS
            );
            preparedStatement.setString(1, theme.getName());
            preparedStatement.setString(2, theme.getDescription());
            preparedStatement.setString(3, theme.getThumbnail());
            return preparedStatement;
        }, keyHolder);
        return theme.persisted(keyHolder.getKey().longValue());
    }

    @Override
    public List<Theme> findAll() {
        return jdbcTemplate.query(SELECT_ALL_SQL, THEME_ROW_MAPPER);
    }

    @Override
    public Optional<Theme> findById(Long id) {
        return jdbcTemplate.query(SELECT_BY_ID_SQL, THEME_ROW_MAPPER, id)
                .stream()
                .findFirst();
    }

    @Override
    public Optional<Theme> findByName(String name) {
        return jdbcTemplate.query(SELECT_BY_NAME_SQL, THEME_ROW_MAPPER, name)
                .stream()
                .findFirst();
    }

    @Override
    public List<PopularTheme> findPopularThemes(
            LocalDate startDate,
            LocalDate endDate,
            int limit
    ) {
        return jdbcTemplate.query(SELECT_POPULAR_SQL, POPULAR_THEME_ROW_MAPPER, startDate, endDate, limit);
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE_SQL, id);
    }
}
