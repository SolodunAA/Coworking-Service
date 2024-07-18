package app.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class BookingResponse {
    private int bookingID;
    private String placeName;
    private int deskNumber;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    public BookingResponse() {
    }

    public BookingResponse(int bookingID, String placeName, int deskNumber, LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.bookingID = bookingID;
        this.placeName = placeName;
        this.deskNumber = deskNumber;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingResponse that = (BookingResponse) o;
        return bookingID == that.bookingID && deskNumber == that.deskNumber && Objects.equals(placeName, that.placeName) && Objects.equals(date, that.date) && Objects.equals(startTime, that.startTime) && Objects.equals(endTime, that.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingID, placeName, deskNumber, date, startTime, endTime);
    }
}

