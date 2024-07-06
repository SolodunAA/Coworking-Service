package app.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReqBookingHallDto {
    private String placeName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    public ReqBookingHallDto() {

    }

    public String getPlaceName() {
        return placeName;
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

    public void setPlaceName(String hallName) {
        this.placeName = hallName;
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
