package app.dto.request;

import java.time.LocalDate;

public class HallAvailableRequest {
    private LocalDate date;

    public HallAvailableRequest() {
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
