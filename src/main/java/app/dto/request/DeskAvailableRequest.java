package app.dto.request;

import java.time.LocalDate;

public class DeskAvailableRequest {
    private String roomName;
    private LocalDate date;

    public DeskAvailableRequest() {
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
