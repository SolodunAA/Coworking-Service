package app.dto;

import java.util.Objects;

public class RoomDto {
    private String roomName;

    public RoomDto() {
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
        RoomDto roomDto = (RoomDto) o;
        return Objects.equals(roomName, roomDto.roomName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomName);
    }
}
