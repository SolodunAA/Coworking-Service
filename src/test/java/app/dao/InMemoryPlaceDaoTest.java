package app.dao;

import app.dao.inMemoryDao.InMemoryPlaceDao;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryPlaceDaoTest {
    @Test
    public void addNewRoom() {
        PlaceDao placeDao = new InMemoryPlaceDao();
        String roomName = "Red";

        var resultMessage = placeDao.addNewRoom(roomName);
        var expectedMessage = roomName + " added";
        assertThat(resultMessage).isEqualTo(expectedMessage);

        var updatedRooms = placeDao.getAllRooms();
        var expectedRooms = Set.of(roomName);
        assertThat(updatedRooms).containsOnlyOnceElementsOf(expectedRooms);
    }

    @Test
    public void addExistingRoom() {
        PlaceDao placeDao = new InMemoryPlaceDao();
        String roomName = "Red";

        placeDao.addNewRoom(roomName);
        var resultMessage = placeDao.addNewRoom(roomName);
        var expectedMessage = "This room already exists";
        assertThat(resultMessage).isEqualTo(expectedMessage);
    }

    @Test
    public void deleteRoom() {
        PlaceDao placeDao = new InMemoryPlaceDao();
        String roomName = "Red";

        placeDao.addNewRoom(roomName);
        var resultMessage = placeDao.deleteRoom(roomName);
        var expectedMessage = roomName + " deleted";
        assertThat(resultMessage).isEqualTo(expectedMessage);

        var currentRooms = placeDao.getAllRooms();
        assertThat(currentRooms.isEmpty()).isTrue();
    }

    @Test
    public void deleteNotExistingRoom() {
        PlaceDao placeDao = new InMemoryPlaceDao();
        String roomName = "Red";

        var resultMessage = placeDao.deleteRoom(roomName);
        var expectedMessage = "This room doesn't exist";
        assertThat(resultMessage).isEqualTo(expectedMessage);

        var currentRooms = placeDao.getAllRooms();
        assertThat(currentRooms.isEmpty()).isTrue();
    }

    @Test
    public void viewAllRooms() {
        PlaceDao placeDao = new InMemoryPlaceDao();
        String roomName = "Red";
        String roomName1 = "Blue";
        String roomName2 = "Green";

        placeDao.addNewRoom(roomName);
        placeDao.addNewRoom(roomName1);
        placeDao.addNewRoom(roomName2);

        var currentRooms = placeDao.getAllRooms();
        var expectedRooms = Set.of(roomName, roomName1, roomName2);
        assertThat(currentRooms).containsOnlyOnceElementsOf(expectedRooms);
    }

    @Test
    public void addNewDesk() {
        PlaceDao placeDao = new InMemoryPlaceDao();
        String roomName = "Red";
        placeDao.addNewRoom(roomName);

        var resultMessage = placeDao.addNewDesk(roomName);
        var expectedMessage = "Desk added";
        assertThat(resultMessage).isEqualTo(expectedMessage);

        var updatedDesks = placeDao.getMapOfAllDesks();
        var expectedDesks = Map.of(1, roomName);
        assertThat(updatedDesks).containsAllEntriesOf(expectedDesks);
    }

    @Test
    public void deleteDesk() {
        PlaceDao placeDao = new InMemoryPlaceDao();
        String roomName = "Red";
        placeDao.addNewRoom(roomName);

        placeDao.addNewDesk(roomName);

        var resultMessage = placeDao.deleteDesk(1);
        var expectedMessage = "Desk deleted";
        assertThat(resultMessage).isEqualTo(expectedMessage);

        var updatedDesks = placeDao.getMapOfAllDesks();
        assertThat(updatedDesks .isEmpty()).isTrue();
    }

    @Test
    public void deleteNotExistingDesk() {
        PlaceDao placeDao = new InMemoryPlaceDao();
        String roomName = "Red";
        placeDao.addNewRoom(roomName);

        placeDao.addNewDesk(roomName);

        var resultMessage = placeDao.deleteDesk(3);
        var expectedMessage = "This desk doesn't exist";
        assertThat(resultMessage).isEqualTo(expectedMessage);
    }

    @Test
    public void viewAllDesks(){
        PlaceDao placeDao = new InMemoryPlaceDao();
        String roomName = "Red";
        String roomName1 = "Blue";

        placeDao.addNewRoom(roomName);
        placeDao.addNewRoom(roomName1);

        placeDao.addNewDesk(roomName);
        placeDao.addNewDesk(roomName);
        placeDao.addNewDesk(roomName1);

        var currentDesk = placeDao.getMapOfAllDesks();
        var expectedDesk = Map.of(1, roomName, 2, roomName, 3, roomName1);

        assertThat(currentDesk ).containsAllEntriesOf(expectedDesk);
    }

    @Test
    public void viewAllDesksInRoom(){
        PlaceDao placeDao = new InMemoryPlaceDao();
        String roomName = "Red";
        String roomName1 = "Blue";

        placeDao.addNewRoom(roomName);
        placeDao.addNewRoom(roomName1);

        placeDao.addNewDesk(roomName);
        placeDao.addNewDesk(roomName);
        placeDao.addNewDesk(roomName1);

        var currentDesk = placeDao.getSetOfAllDesksInRoom(roomName);
        var expectedDesk = Set.of(1, 2);
        assertThat(currentDesk).containsOnlyOnceElementsOf(expectedDesk);

        var currentDesk1 = placeDao.getSetOfAllDesksInRoom(roomName1);
        var expectedDesk1 = Set.of(3);
        assertThat(currentDesk1).containsOnlyOnceElementsOf(expectedDesk1);
    }

    @Test
    public void addNewHall(){
        PlaceDao placeDao = new InMemoryPlaceDao();
        String hallName = "GrandHall";

        var resultMessage = placeDao.addNewHall(hallName);
        var expectedMessage = hallName + " added";
        assertThat(resultMessage).isEqualTo(expectedMessage);

        var updatedHalls = placeDao.getMapOfAllHalls();
        var expectedHalls = Map.of(1, hallName);
        assertThat(updatedHalls).containsAllEntriesOf(expectedHalls);
    }

    @Test
    public void addExistingHall(){
        PlaceDao placeDao = new InMemoryPlaceDao();
        String hallName = "GrandHall";
        placeDao.addNewHall(hallName);

        var resultMessage = placeDao.addNewHall(hallName);
        var expectedMessage = "This hall already exists";
        assertThat(resultMessage).isEqualTo(expectedMessage);

    }

    @Test
    public void deleteHall(){
        PlaceDao placeDao = new InMemoryPlaceDao();
        String hallName = "GrandHall";
        placeDao.addNewHall(hallName);

        var idToDelete = placeDao.getMapOfAllHalls().entrySet().stream()
                .filter(entry -> entry.getValue().equals(hallName))
                .map(Map.Entry::getKey)
                .findAny()
                .orElseThrow();

        var resultMessage = placeDao.deleteHall(idToDelete);
        var expectedMessage = "Hall deleted";
        assertThat(resultMessage).isEqualTo(expectedMessage);
        var currentHalls = placeDao.getMapOfAllHalls();
        assertThat(currentHalls.isEmpty()).isTrue();

    }

    @Test
    public void deleteNotExistingHall(){
        PlaceDao placeDao = new InMemoryPlaceDao();

        var resultMessage = placeDao.deleteHall(2);
        var expectedMessage = "This hall doesn't exist";
        assertThat(resultMessage).isEqualTo(expectedMessage);

    }

    @Test
    public void viewAllHalls(){
        PlaceDao placeDao = new InMemoryPlaceDao();
        String hallName = "GrandHall";
        String hallName1 = "SmallHall";

        placeDao.addNewHall(hallName);
        placeDao.addNewHall(hallName1);

        var updatedHalls = placeDao.getMapOfAllHalls();
        var expectedHalls = Map.of(1, hallName, 2, hallName1);

        assertThat(updatedHalls).containsAllEntriesOf(expectedHalls);

    }

    @Test
    public void viewAllPlacesId(){
        PlaceDao placeDao = new InMemoryPlaceDao();
        String hallName = "GrandHall";
        String hallName1 = "SmallHall";
        String roomName = "Red";

        placeDao.addNewRoom(roomName);
        placeDao.addNewDesk(roomName);
        placeDao.addNewDesk(roomName);
        placeDao.addNewDesk(roomName);
        placeDao.addNewHall(hallName);
        placeDao.addNewHall(hallName1);
        placeDao.deleteDesk(3);

        var updatedPlaces = placeDao.getSetAllPlacesId();
        var expectedPlaces = Set.of(1, 2, 4, 5);

        assertThat(updatedPlaces).containsOnlyOnceElementsOf(expectedPlaces);
    }

}
