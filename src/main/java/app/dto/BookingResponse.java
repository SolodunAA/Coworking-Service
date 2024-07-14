package app.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class BookingResponse {
    private int bookingID;
    private String placeName;
    private int deskNumber;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    public BookingResponse() {
    }


    public String getPlaceName() {
        return placeName;
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


    public int getBookingID() {
        return bookingID;
    }

    public void setBookingID(int bookingID) {
        this.bookingID = bookingID;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
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

