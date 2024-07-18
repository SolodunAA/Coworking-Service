package app.dto.request;

import java.util.Objects;

public class RoomRequest {
    private String roomName;

    public RoomRequest() {
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomRequest that = (RoomRequest) o;
        return Objects.equals(roomName, that.roomName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomName);
    }
}
