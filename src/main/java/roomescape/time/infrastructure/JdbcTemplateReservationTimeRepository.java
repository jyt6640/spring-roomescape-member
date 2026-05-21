package roomescape.time.infrastructure;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.domain.ReservationTimeRepository;

@Repository
public class JdbcTemplateReservationTimeRepository implements ReservationTimeRepository {

    private static final String INSERT_SQL = """
            INSERT INTO reservation_time(start_at)
            VALUES (?)
            """;
    private static final String INSERT_WITH_ID_SQL = """
            INSERT INTO reservation_time(id, start_at)
            VALUES (?, ?)
            """;
    private static final String SELECT_ALL_SQL = """
            SELECT id, start_at
            FROM reservation_time
            ORDER BY id
            """;
    private static final String SELECT_BY_ID_SQL = """
            SELECT id, start_at
            FROM reservation_time
            WHERE id = ?
            """;
    private static final String SELECT_BY_START_AT_SQL = """
            SELECT id, start_at
            FROM reservation_time
            WHERE start_at = ?
            """;
    private static final String DELETE_SQL = """
            DELETE FROM reservation_time
            WHERE id = ?
            """;
    private static final RowMapper<ReservationTime> ROW_MAPPER = (resultSet, rowNumber) -> ReservationTime.restore(
            resultSet.getLong("id"),
            resultSet.getString("start_at")
    );

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateReservationTimeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ReservationTime save(ReservationTime reservationTime) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    INSERT_SQL,
                    Statement.RETURN_GENERATED_KEYS
            );
            preparedStatement.setString(1, reservationTime.getStartAt());
            return preparedStatement;
        }, keyHolder);
        return reservationTime.persisted(keyHolder.getKey().longValue());
    }

    @Override
    public ReservationTime saveWithIdIfAbsent(Long id) {
        Optional<ReservationTime> reservationTime = findById(id);
        if (reservationTime.isPresent()) {
            return reservationTime.get();
        }
        jdbcTemplate.update(INSERT_WITH_ID_SQL, id, "10:00");
        return ReservationTime.restore(id, "10:00");
    }

    @Override
    public List<ReservationTime> findAll() {
        return jdbcTemplate.query(SELECT_ALL_SQL, ROW_MAPPER);
    }

    @Override
    public Optional<ReservationTime> findById(Long id) {
        return jdbcTemplate.query(SELECT_BY_ID_SQL, ROW_MAPPER, id)
                .stream()
                .findFirst();
    }

    @Override
    public Optional<ReservationTime> findByStartAt(String startAt) {
        return jdbcTemplate.query(SELECT_BY_START_AT_SQL, ROW_MAPPER, startAt)
                .stream()
                .findFirst();
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE_SQL, id);
    }
}
