package app.dto;

import java.time.LocalDate;

public class ReqAvailableDeskDto {
    private String roomName;
    private LocalDate date;

    public ReqAvailableDeskDto() {
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
