package app.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReqBookingDeskDto {
    private String roomName;
    private int deskNumber;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    public ReqBookingDeskDto() {

    }

    public String getRoomName() {
        return roomName;
    }

    public int getDeskNumber() {
        return deskNumber;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setDeskNumber(int deskNumber) {
        this.deskNumber = deskNumber;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
