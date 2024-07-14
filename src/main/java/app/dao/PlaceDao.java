package app.dao;

import java.util.Set;

/**
 * storing information about all existing desks
 */
public interface PlaceDao {

    /**
     * adding a new place in coworking area
     * @param placeName name of new place
     * @param placeType type of new place
     * @return actions status
     */
    String addNewPlace(String placeName, String placeType);

    /**
     * deleting room or hall from coworking area
     * @param placeName name of place for deleting
     * @return actions status
     */
    String deletePlace(String placeName);

    /**
     * adding a new extra desktop to a room
     * @param roomName the name of the room where the new desk is added
     * @return actions status
     */
    String addNewDesk(String roomName);

    /**
     * deleting the desk from the room
     * @param deskNumber the number desk in the room
     * @param roomName the name of the room there is the desk
     * @return actions status
     */
    String deleteDesk(int deskNumber, String roomName);

    /**
     * get all rooms in coworking
     * @return set of name rooms
     */
    Set<String> getAllRooms();

    /**
     * showing of all existing desks in room
     * @param roomName - the name of room
     * @return set of all tables in the room
     */
    Set<Integer> getSetOfAllDesksInRoom(String roomName);

    /**
     * showing of all existing halls in coworking place
     * @return set all halls
     */
    Set<String> getAllHalls();
    /**
     * check if place already exists in coworking
     * @param placeName name of place
     * @return true if place already exists, false - not
     */
    boolean isPlaceExists(String placeName);
    /**
     * check if desk already exists in the room
     * @param roomName name of room
     * @return true if desk already exists, false - not
     */
    boolean isDeskExistsInRoom(String roomName, int deskNumber);

}
