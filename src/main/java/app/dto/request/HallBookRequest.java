package app.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HallBookRequest that = (HallBookRequest) o;
        return Objects.equals(hallName, that.hallName) && Objects.equals(date, that.date) && Objects.equals(startTime, that.startTime) && Objects.equals(endTime, that.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hallName, date, startTime, endTime);
    }
}
