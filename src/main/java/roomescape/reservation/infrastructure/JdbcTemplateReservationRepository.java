package roomescape.reservation.infrastructure;

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
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.ReservationTime;

@Repository
public class JdbcTemplateReservationRepository implements ReservationRepository {

    private static final String INSERT_SQL = """
            INSERT INTO reservation(name, date, time, time_id, theme_id)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String SELECT_BASE_SQL = """
            SELECT
                r.id AS reservation_id,
                r.name AS reservation_name,
                r.date AS reservation_date,
                r.time AS legacy_time,
                rt.id AS time_id,
                rt.start_at AS start_at,
                t.id AS theme_id,
                t.name AS theme_name,
                t.description AS theme_description,
                t.thumbnail AS theme_thumbnail
            FROM reservation AS r
            LEFT JOIN reservation_time AS rt
                ON r.time_id = rt.id
            LEFT JOIN theme AS t
                ON r.theme_id = t.id
            """;
    private static final String SELECT_ALL_SQL = SELECT_BASE_SQL + """
            ORDER BY r.id
            """;
    private static final String SELECT_BY_ID_SQL = SELECT_BASE_SQL + """
            WHERE r.id = ?
            """;
    private static final String SELECT_BY_NAME_SQL = SELECT_BASE_SQL + """
            WHERE r.name = ?
            ORDER BY r.id
            """;
    private static final String EXISTS_BY_SCHEDULE_SQL = """
            SELECT COUNT(1)
            FROM reservation
            WHERE date = ? AND time_id = ? AND theme_id = ?
            """;
    private static final String EXISTS_BY_SCHEDULE_EXCEPT_ID_SQL = """
            SELECT COUNT(1)
            FROM reservation
            WHERE date = ? AND time_id = ? AND theme_id = ? AND id <> ?
            """;
    private static final String EXISTS_BY_TIME_ID_SQL = """
            SELECT COUNT(1)
            FROM reservation
            WHERE time_id = ?
            """;
    private static final String SELECT_RESERVED_TIME_IDS_SQL = """
            SELECT time_id
            FROM reservation
            WHERE date = ? AND theme_id = ?
            """;
    private static final String UPDATE_SCHEDULE_SQL = """
            UPDATE reservation
            SET date = ?, time = ?, time_id = ?
            WHERE id = ?
            """;
    private static final String DELETE_SQL = """
            DELETE FROM reservation
            WHERE id = ?
            """;
    private static final RowMapper<Reservation> ROW_MAPPER = (resultSet, rowNumber) -> {
        Long timeId = resultSet.getObject("time_id", Long.class);
        Long themeId = resultSet.getObject("theme_id", Long.class);
        String startAt = resultSet.getString("start_at");
        if (startAt == null) {
            startAt = resultSet.getString("legacy_time");
        }
        ReservationTime time = ReservationTime.restore(timeId, startAt);
        Theme theme = restoreTheme(themeId, resultSet);
        return Reservation.restore(
                resultSet.getLong("reservation_id"),
                resultSet.getString("reservation_name"),
                resultSet.getString("reservation_date"),
                time,
                theme
        );
    };

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateReservationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Reservation save(Reservation reservation) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> prepareInsertStatement(connection, reservation), keyHolder);
        return reservation.persisted(keyHolder.getKey().longValue());
    }

    @Override
    public List<Reservation> findAll() {
        return jdbcTemplate.query(SELECT_ALL_SQL, ROW_MAPPER);
    }

    @Override
    public List<Reservation> findByName(String name) {
        return jdbcTemplate.query(SELECT_BY_NAME_SQL, ROW_MAPPER, name);
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        return jdbcTemplate.query(SELECT_BY_ID_SQL, ROW_MAPPER, id)
                .stream()
                .findFirst();
    }

    @Override
    public boolean existsByDateAndTimeIdAndThemeId(
            LocalDate date,
            Long timeId,
            Long themeId
    ) {
        Integer count = jdbcTemplate.queryForObject(EXISTS_BY_SCHEDULE_SQL, Integer.class, date, timeId, themeId);
        return count != null && count > 0;
    }

    @Override
    public boolean existsByDateAndTimeIdAndThemeIdExceptId(
            LocalDate date,
            Long timeId,
            Long themeId,
            Long id
    ) {
        Integer count = jdbcTemplate.queryForObject(
                EXISTS_BY_SCHEDULE_EXCEPT_ID_SQL,
                Integer.class,
                date,
                timeId,
                themeId,
                id
        );
        return count != null && count > 0;
    }

    @Override
    public boolean existsByTimeId(Long timeId) {
        Integer count = jdbcTemplate.queryForObject(EXISTS_BY_TIME_ID_SQL, Integer.class, timeId);
        return count != null && count > 0;
    }

    @Override
    public List<Long> findReservedTimeIds(
            LocalDate date,
            Long themeId
    ) {
        return jdbcTemplate.queryForList(SELECT_RESERVED_TIME_IDS_SQL, Long.class, date, themeId);
    }

    @Override
    public void updateSchedule(Reservation reservation) {
        jdbcTemplate.update(
                UPDATE_SCHEDULE_SQL,
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                reservation.getTime().getId(),
                reservation.getId()
        );
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE_SQL, id);
    }

    private PreparedStatement prepareInsertStatement(
            java.sql.Connection connection,
            Reservation reservation
    ) throws java.sql.SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, reservation.getName());
        preparedStatement.setString(2, reservation.getDate());
        preparedStatement.setString(3, reservation.getTime().getStartAt());
        preparedStatement.setLong(4, reservation.getTime().getId());
        preparedStatement.setLong(5, reservation.getTheme().getId());
        return preparedStatement;
    }

    private static Theme restoreTheme(
            Long themeId,
            java.sql.ResultSet resultSet
    ) throws java.sql.SQLException {
        if (themeId == null) {
            return Theme.restore(
                    null,
                    "기본 테마",
                    "관리자 예약 기본 테마",
                    "https://example.com/default-theme.png"
            );
        }
        return Theme.restore(
                themeId,
                resultSet.getString("theme_name"),
                resultSet.getString("theme_description"),
                resultSet.getString("theme_thumbnail")
        );
    }
}
