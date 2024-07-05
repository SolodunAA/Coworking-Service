package app.dto;

import java.time.LocalDate;

public class ReqAvailableHallDto {
    private LocalDate localDate;

    public ReqAvailableHallDto() {
    }

    public LocalDate getLocalDate() {
        return localDate;
    }
    public void setDate(LocalDate date) {
        this.localDate = date;
    }
}
