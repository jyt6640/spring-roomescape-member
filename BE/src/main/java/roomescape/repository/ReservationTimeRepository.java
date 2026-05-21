package roomescape.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.domain.ReservationTime;

@Repository
public class ReservationTimeRepository {

    private final JdbcTemplate jdbcTemplate;

    public ReservationTimeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ReservationTime> findAll() {
        return jdbcTemplate.query(
                "SELECT id, start_at FROM reservation_time ORDER BY id",
                (rs, rowNum) -> new ReservationTime(rs.getLong("id"), rs.getString("start_at"))
        );
    }

    public Optional<ReservationTime> findById(Long id) {
        try {
            ReservationTime time = jdbcTemplate.queryForObject(
                    "SELECT id, start_at FROM reservation_time WHERE id = ?",
                    (rs, rowNum) -> new ReservationTime(rs.getLong("id"), rs.getString("start_at")),
                    id
            );
            return Optional.ofNullable(time);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    public Optional<ReservationTime> findByStartAt(String startAt) {
        try {
            ReservationTime time = jdbcTemplate.queryForObject(
                    "SELECT id, start_at FROM reservation_time WHERE start_at = ?",
                    (rs, rowNum) -> new ReservationTime(rs.getLong("id"), rs.getString("start_at")),
                    startAt
            );
            return Optional.ofNullable(time);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    public ReservationTime save(String startAt) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO reservation_time(start_at) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            statement.setString(1, startAt);
            return statement;
        }, keyHolder);
        return new ReservationTime(keyHolder.getKey().longValue(), startAt);
    }

    public ReservationTime saveWithId(Long id, String startAt) {
        jdbcTemplate.update("INSERT INTO reservation_time(id, start_at) VALUES (?, ?)", id, startAt);
        return new ReservationTime(id, startAt);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM reservation_time WHERE id = ?", id);
    }
}
