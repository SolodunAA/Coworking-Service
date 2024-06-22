package app.dao.inMemoryDao;

import app.dao.PlaceDao;

import java.util.*;

public class InMemoryPlaceDao implements PlaceDao {
    private final Map<Integer, String> desksStorage = new HashMap<>();
    private final Map<Integer, String> hallStorage = new HashMap<>();
    private final Set<String> roomStorage = new HashSet<>();
    private int placeId = 0;

    @Override
    public String addNewRoom(String roomName) {
        if (roomStorage.contains(roomName)) {
            return "This room already exists";
        } else {
            roomStorage.add(roomName);
            return roomName + " added";
        }
    }

    @Override
    public String deleteRoom(String roomName) {
        if (roomStorage.contains(roomName)) {
            roomStorage.remove(roomName);
            var iterator = desksStorage.entrySet().iterator();
            while (iterator.hasNext()) {
                var next = iterator.next();
                if (next.getValue().equals(roomName)) {
                    iterator.remove();
                }
            }
            return roomName + " deleted";
        } else {
            return "This room doesn't exist";
        }

    }

    public Set<String> getAllRooms() {
        return roomStorage;
    }

    @Override
    public String addNewDesk(String roomName) {
        if (roomStorage.contains(roomName)) {
            desksStorage.put(++placeId, roomName);
            return "Desk added";
        } else {
            return "This room doesn't exist";
        }
    }

    @Override
    public String deleteDesk(int deskId) {
        if (desksStorage.containsKey(deskId)) {
            desksStorage.remove(deskId);
            return "Desk deleted";
        } else {
            return "This desk doesn't exist";
        }
    }

    @Override
    public Map<Integer, String> getMapOfAllDesks() {
        return Map.copyOf(desksStorage);
    }

    @Override
    public Set<Integer> getSetOfAllDesksInRoom(String roomName) {
        Set<Integer> desksInRoom = new HashSet<>();
        Set<Integer> allDesksId = desksStorage.keySet();
        for (int deskId : allDesksId) {
            if (desksStorage.get(deskId).equals(roomName)) {
                desksInRoom.add(deskId);
            }
        }
        return desksInRoom;
    }

    @Override
    public String addNewHall(String hallName) {
        if (hallStorage.containsValue(hallName)) {
            return "This hall already exists";
        } else {
            hallStorage.put(++placeId, hallName);
            return hallName + " added";
        }
    }

    @Override
    public String deleteHall(int placeId) {
        if (hallStorage.containsKey(placeId)) {
            hallStorage.remove(placeId);
            return "Hall deleted";
        } else {
            return "This hall doesn't exist";
        }
    }

    @Override
    public Map<Integer, String> getMapOfAllHalls() {
        return Map.copyOf(hallStorage);
    }


    @Override
    public Set<Integer> getSetAllPlacesId() {
        Set<Integer> allPlaces = new HashSet<>();
        allPlaces.addAll(hallStorage.keySet());
        allPlaces.addAll(getMapOfAllDesks().keySet());
        return allPlaces;
    }
}
