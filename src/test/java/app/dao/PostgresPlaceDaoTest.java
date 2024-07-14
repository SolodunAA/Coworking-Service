package app.dao;

import app.dao.postgresDao.PostgresPlaceDao;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class PostgresPlaceDaoTest {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15-alpine"
    );

    private final PlaceDao placeDao = new PostgresPlaceDao(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());

    @BeforeAll
    public static void beforeAll() {
        postgres.start();
        runMigrations();
    }

    private static void runMigrations() {
        try (Connection connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
            System.out.println("starting migration");
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase =
                    new Liquibase("db.changelog/changelog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update();
            System.out.println("migration finished successfully");
        } catch (Exception e ){
            System.out.println("Got SQL Exception " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    public static void afterAll() {
        postgres.stop();
    }

    @AfterEach
    public void clearDb() {
        try (Connection connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
            var st = connection.createStatement();
            st.execute("TRUNCATE TABLE coworking.place_table CASCADE");
            st.execute("TRUNCATE TABLE coworking.desk_table CASCADE");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    @DisplayName("checking to add new hall")
    public void addNewHallTest(){
        String hallName = "Moscow";
        String placeType = "hall";

        placeDao.addNewPlace(hallName, placeType);

        assertThat(placeDao.getAllHalls().contains(hallName)).isTrue();
    }
    @Test
    @DisplayName("checking to add new room")
    public void addNewRoomTest(){
        String roomName = "Violet";
        String placeType = "room";

        placeDao.addNewPlace(roomName, placeType);

        assertThat(placeDao.getAllRooms().contains(roomName)).isTrue();
    }

    @Test
    @DisplayName("checking to add new desk")
    public void addNewDeskTest(){
        String roomName = "Red";
        String placeTypeRoom = "room";

        placeDao.addNewPlace(roomName, placeTypeRoom);
        placeDao.addNewDesk(roomName);
        placeDao.addNewDesk(roomName);

        assertThat(placeDao.getSetOfAllDesksInRoom(roomName).size()).isEqualTo(2);
    }
    @Test
    @DisplayName("checking to delete the place")
    public void deletePlaceTest(){
        String roomName = "Violet";
        String placeTypeRoom = "room";
        String hallName = "Moscow";
        String placeTypeHall = "hall";

        placeDao.addNewPlace(roomName, placeTypeRoom);
        placeDao.addNewPlace(hallName, placeTypeHall);

        placeDao.deletePlace(roomName);
        placeDao.deletePlace(hallName);

        assertThat(placeDao.getAllRooms().contains(roomName)).isFalse();
        assertThat(placeDao.getAllHalls().contains(hallName)).isFalse();
    }
    @Test
    @DisplayName("checking to delete the desk in the room")
    public void deleteDeskTest() {
        String roomName = "Violet";
        String placeTypeRoom = "room";
        int deskNumber = 1;

        placeDao.addNewPlace(roomName, placeTypeRoom);
        placeDao.addNewDesk(roomName);
        placeDao.deleteDesk(deskNumber, roomName);

        assertThat(placeDao.getSetOfAllDesksInRoom(roomName).isEmpty()).isTrue();
    }

    @Test
    @DisplayName("checking to get set of all halls in coworking")
    public void getAllHallsTest() {
        String hallName1 = "Moscow";
        String hallName2 = "Paris";
        String placeTypeHall = "hall";
        Set<String> halls = Set.of(hallName1, hallName2);

        placeDao.addNewPlace(hallName1, placeTypeHall);
        placeDao.addNewPlace(hallName2, placeTypeHall);

        assertThat(placeDao.getAllHalls()).isEqualTo(halls);
    }

    @Test
    @DisplayName("checking to get set of all halls in coworking")
    public void getAllRoomsTest() {
        String roomName1 = "Red";
        String roomName2 = "Blue";
        String placeTypeRoom = "room";
        Set<String> rooms = Set.of(roomName1, roomName2);

        placeDao.addNewPlace(roomName1, placeTypeRoom);
        placeDao.addNewPlace(roomName2, placeTypeRoom);

        assertThat(placeDao.getAllRooms()).isEqualTo(rooms);
    }
    @Test
    @DisplayName("checking to get set of all desks in room")
    public void getSetOfAllDesksInRoomTest() {
        String roomName = "Red";
        String placeTypeRoom = "room";
        Set<Integer> desks = Set.of(1, 2, 3);

        placeDao.addNewPlace(roomName, placeTypeRoom);
        placeDao.addNewDesk(roomName);
        placeDao.addNewDesk(roomName);
        placeDao.addNewDesk(roomName);

        assertThat(placeDao.getSetOfAllDesksInRoom(roomName)).isEqualTo(desks);
    }
}
