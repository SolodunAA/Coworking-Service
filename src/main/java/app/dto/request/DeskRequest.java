package app.dto.request;

import java.util.Objects;

public class DeskRequest {
    private String roomName;
    private int deskNumber;

    public DeskRequest() {
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getDeskNumber() {
        return deskNumber;
    }

    public void setDeskNumber(int deskNumber) {
        this.deskNumber = deskNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeskRequest that = (DeskRequest) o;
        return deskNumber == that.deskNumber && Objects.equals(roomName, that.roomName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomName, deskNumber);
    }
}
