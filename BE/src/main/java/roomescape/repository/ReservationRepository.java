package roomescape.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;

@Repository
public class ReservationRepository {

    private final JdbcTemplate jdbcTemplate;

    public ReservationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Reservation> findAll() {
        return jdbcTemplate.query(findReservationSql("ORDER BY r.id"), this::mapReservation);
    }

    public List<Reservation> findByName(String name) {
        return jdbcTemplate.query(findReservationSql("WHERE r.name = ? ORDER BY r.date, rt.start_at"), this::mapReservation, name);
    }

    public Optional<Reservation> findById(Long id) {
        try {
            Reservation reservation = jdbcTemplate.queryForObject(
                    findReservationSql("WHERE r.id = ?"),
                    this::mapReservation,
                    id
            );
            return Optional.ofNullable(reservation);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    public Reservation save(String name, String date, Long timeId, Long themeId) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO reservation(name, date, time_id, theme_id) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            statement.setString(1, name);
            statement.setString(2, date);
            statement.setLong(3, timeId);
            statement.setLong(4, themeId);
            return statement;
        }, keyHolder);
        return findById(keyHolder.getKey().longValue()).orElseThrow();
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM reservation WHERE id = ?", id);
    }

    public void updateDateAndTime(Long id, String date, Long timeId) {
        jdbcTemplate.update("UPDATE reservation SET date = ?, time_id = ? WHERE id = ?", date, timeId, id);
    }

    public boolean existsByDateTimeTheme(String date, Long timeId, Long themeId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM reservation WHERE date = ? AND time_id = ? AND theme_id = ?",
                Integer.class,
                date,
                timeId,
                themeId
        );
        return count != null && count > 0;
    }

    public boolean existsByDateTimeThemeExcludingId(String date, Long timeId, Long themeId, Long excludedId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM reservation WHERE date = ? AND time_id = ? AND theme_id = ? AND id <> ?",
                Integer.class,
                date,
                timeId,
                themeId,
                excludedId
        );
        return count != null && count > 0;
    }

    public boolean existsByTimeId(Long timeId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM reservation WHERE time_id = ?",
                Integer.class,
                timeId
        );
        return count != null && count > 0;
    }

    public boolean existsByThemeId(Long themeId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM reservation WHERE theme_id = ?",
                Integer.class,
                themeId
        );
        return count != null && count > 0;
    }

    private String findReservationSql(String clause) {
        return """
                SELECT
                    r.id AS reservation_id,
                    r.name,
                    r.date,
                    rt.id AS time_id,
                    rt.start_at,
                    t.id AS theme_id,
                    t.name AS theme_name,
                    t.description,
                    t.thumbnail_url
                FROM reservation r
                JOIN reservation_time rt ON r.time_id = rt.id
                JOIN theme t ON r.theme_id = t.id
                """
                + clause;
    }

    private Reservation mapReservation(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        ReservationTime time = new ReservationTime(rs.getLong("time_id"), rs.getString("start_at"));
        Theme theme = new Theme(
                rs.getLong("theme_id"),
                rs.getString("theme_name"),
                rs.getString("description"),
                rs.getString("thumbnail_url")
        );
        return new Reservation(
                rs.getLong("reservation_id"),
                rs.getString("name"),
                rs.getString("date"),
                time,
                theme
        );
    }
}
