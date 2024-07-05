package app.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReqBookingHallDto {
    private String hallName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    public ReqBookingHallDto() {

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
        if (date.isBefore(LocalDate.now()) || date.isAfter(LocalDate.now().plusYears(1))) {
            throw new IllegalArgumentException("date should be between today and today + 1y");
        }
        this.date = date;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

}
