package app.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class Booking {
    private final int bookingID;
    private final String userLogin;
    private final String placeName;
    private final int deskNumber;
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public Booking(int bookingID, String userLogin, String placeName, int deskNumber, LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.bookingID = bookingID;
        this.userLogin = userLogin;
        this.placeName = placeName;
        this.deskNumber = deskNumber;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getUserLogin() {
        return userLogin;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return bookingID == booking.bookingID && Objects.equals(userLogin, booking.userLogin) && Objects.equals(placeName, booking.placeName) && Objects.equals(deskNumber, booking.deskNumber) && Objects.equals(date, booking.date) && Objects.equals(startTime, booking.startTime) && Objects.equals(endTime, booking.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingID, userLogin, placeName, deskNumber, date, startTime, endTime);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingID=" + bookingID +
                ", userLogin='" + userLogin + '\'' +
                ", placeName='" + placeName + '\'' +
                ", deskNumber=" + deskNumber +
                ", date=" + date +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
