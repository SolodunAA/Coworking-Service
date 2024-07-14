package app.dto.request;

public class BookingDeleteRequest {
    private int bookingId;

    public BookingDeleteRequest() {
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }
}
