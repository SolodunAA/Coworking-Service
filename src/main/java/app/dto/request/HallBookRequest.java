package app.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

public class HallBookRequest {
    private String hallName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    public HallBookRequest() {
    }

    public String getHallName() {
        return hallName;
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

    public void setHallName(String hallName) {
        this.hallName = hallName;
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
