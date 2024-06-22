package app.dao;

import java.util.Map;
import java.util.Set;

/**
 * storing information about all existing desks
 */
public interface PlaceDao {
    /**
     * adding a new room
     * @param roomName the name of new room
     * @return actions status
     */
    String addNewRoom(String roomName);
    /**
     * delete the room
     * @param roomName the name of deleting room
     * @return actions status
     */
    String deleteRoom(String roomName);

    /**
     * get all rooms in coworking
     * @return set of name rooms
     */
    Set<String> getAllRooms();
    /**
     * adding a new extra desktop to a room
     * @param roomName the name of the room where the new desk is added
     * @return actions status
     */
    String addNewDesk(String roomName);
    /**
     * deleting the desk from the room
     * @param deskId the id of the removed desk
     * @return actions status
     */
    String deleteDesk(int deskId);
    /**
     * showing of all existing desks in coworking place
     * @return map, there key is id desk, value is name of room
     */
    Map<Integer, String> getMapOfAllDesks();
    /**
     * showing of all existing desks in room
     * @param roomName - the name of room
     * @return set of all tables in the room
     */
    Set<Integer> getSetOfAllDesksInRoom(String roomName);
    /**
     * adding a new extra halls
     * @param hallName the name of the hall is added
     * @return actions status
     */
    String addNewHall(String hallName);
    /**
     * deleting the hall
     * @param placeId the id of the hall is removed
     * @return actions status
     */
    String deleteHall(int placeId);

    /**
     * showing of all existing halls in coworking place
     * @return set all halls
     */
    Map<Integer, String> getMapOfAllHalls();

    /**
     * showing of all existing halls and desks in coworking place
     * @return set of places
     */
    Set<Integer> getSetAllPlacesId();


}
