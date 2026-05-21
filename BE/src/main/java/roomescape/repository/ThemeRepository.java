package roomescape.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.domain.PopularTheme;
import roomescape.domain.Theme;

@Repository
public class ThemeRepository {

    private final JdbcTemplate jdbcTemplate;

    public ThemeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Theme> findAll() {
        return jdbcTemplate.query(
                "SELECT id, name, description, thumbnail_url FROM theme ORDER BY id",
                (rs, rowNum) -> new Theme(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("thumbnail_url")
                )
        );
    }

    public Optional<Theme> findById(Long id) {
        try {
            Theme theme = jdbcTemplate.queryForObject(
                    "SELECT id, name, description, thumbnail_url FROM theme WHERE id = ?",
                    (rs, rowNum) -> new Theme(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getString("thumbnail_url")
                    ),
                    id
            );
            return Optional.ofNullable(theme);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    public Theme save(String name, String description, String thumbnailUrl) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO theme(name, description, thumbnail_url) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            statement.setString(1, name);
            statement.setString(2, description);
            statement.setString(3, thumbnailUrl);
            return statement;
        }, keyHolder);
        return new Theme(keyHolder.getKey().longValue(), name, description, thumbnailUrl);
    }

    public Theme saveWithId(Long id, String name, String description, String thumbnailUrl) {
        jdbcTemplate.update(
                "INSERT INTO theme(id, name, description, thumbnail_url) VALUES (?, ?, ?, ?)",
                id,
                name,
                description,
                thumbnailUrl
        );
        return new Theme(id, name, description, thumbnailUrl);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM theme WHERE id = ?", id);
    }

    public List<PopularTheme> findPopularThemes(String startDate, String endDate) {
        return jdbcTemplate.query(
                """
                        SELECT t.id, t.name, t.description, t.thumbnail_url, COUNT(r.id) AS reservation_count
                        FROM theme t
                        JOIN reservation r ON r.theme_id = t.id
                        WHERE r.date BETWEEN ? AND ?
                        GROUP BY t.id, t.name, t.description, t.thumbnail_url
                        ORDER BY reservation_count DESC, t.id ASC
                        LIMIT 10
                        """,
                (rs, rowNum) -> new PopularTheme(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("thumbnail_url"),
                        rs.getInt("reservation_count")
                ),
                startDate,
                endDate
        );
    }
}
