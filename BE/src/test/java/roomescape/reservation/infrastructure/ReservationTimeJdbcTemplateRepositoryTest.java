package roomescape.reservation.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalTime;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import roomescape.reservation.entity.ReservationTime;

class ReservationTimeJdbcTemplateRepositoryTest {

    private ReservationTimeJdbcTemplateRepository reservationTimeRepository;

    @BeforeEach
    void setUp() {
        DataSource dataSource = createDataSource();
        initializeDatabase(dataSource);
        reservationTimeRepository = new ReservationTimeJdbcTemplateRepository(dataSource);
    }

    private DataSource createDataSource() {
        String databaseName = "reservation-time-repository-test-" + System.nanoTime();
        return new DriverManagerDataSource(
                "jdbc:h2:mem:" + databaseName + ";DB_CLOSE_DELAY=-1",
                "sa",
                ""
        );
    }

    private void initializeDatabase(DataSource dataSource) {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(
                new ClassPathResource("schema.sql")
        );
        databasePopulator.execute(dataSource);
    }

    @Test
    @DisplayName("예약 시간을 저장한다")
    void save() {
        // given
        ReservationTime reservationTime = ReservationTime.createWithNullId(LocalTime.of(10, 0));

        // when
        ReservationTime savedTime = reservationTimeRepository.save(reservationTime);

        // then
        assertThat(savedTime.id()).isNotNull();
        assertThat(savedTime.startAt()).isEqualTo(LocalTime.of(10, 0));
    }

    @Test
    @DisplayName("id로 예약 시간을 조회한다")
    void findById() {
        // given
        ReservationTime savedTime = reservationTimeRepository.save(
                ReservationTime.createWithNullId(LocalTime.of(10, 0))
        );

        // when
        ReservationTime foundTime = reservationTimeRepository.findById(savedTime.id()).orElseThrow();

        // then
        assertThat(foundTime).isEqualTo(savedTime);
    }

    @Test
    @DisplayName("예약 시간 전체 목록을 조회한다")
    void findAll() {
        // given
        ReservationTime firstTime = reservationTimeRepository.save(
                ReservationTime.createWithNullId(LocalTime.of(10, 0))
        );
        ReservationTime secondTime = reservationTimeRepository.save(
                ReservationTime.createWithNullId(LocalTime.of(11, 0))
        );

        // when
        List<ReservationTime> times = reservationTimeRepository.findAll();

        // then
        assertThat(times).contains(firstTime, secondTime);
    }

    @Test
    @DisplayName("예약 시간을 삭제한다")
    void deleteById() {
        // given
        ReservationTime savedTime = reservationTimeRepository.save(
                ReservationTime.createWithNullId(LocalTime.of(10, 0))
        );

        // when
        reservationTimeRepository.deleteById(savedTime.id());

        // then
        assertThat(reservationTimeRepository.findById(savedTime.id())).isEmpty();
    }
}
