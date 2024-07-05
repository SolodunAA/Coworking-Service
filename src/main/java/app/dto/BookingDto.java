package app.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class BookingDto {
    private int bookingID;
    private String userLogin;
    private String placeName;
    private int deskNumber;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    public BookingDto(int bookingID, String userLogin, String placeName, int deskNumber, LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.bookingID = bookingID;
        this.userLogin = userLogin;
        this.placeName = placeName;
        this.deskNumber = deskNumber;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    public BookingDto() {
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
        BookingDto bookingDto = (BookingDto) o;
        return bookingID == bookingDto.bookingID && Objects.equals(userLogin, bookingDto.userLogin) && Objects.equals(placeName, bookingDto.placeName) && Objects.equals(deskNumber, bookingDto.deskNumber) && Objects.equals(date, bookingDto.date) && Objects.equals(startTime, bookingDto.startTime) && Objects.equals(endTime, bookingDto.endTime);
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

    public void setBookingID(int bookingID) {
        this.bookingID = bookingID;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
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
