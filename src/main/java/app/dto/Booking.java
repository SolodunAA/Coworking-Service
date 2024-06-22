package app.dto;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Booking {
    private final String userLogin;
    private final int placeID;
    private final LocalDate date;
    private final int time;
    private final int period;
    private final Set<Integer> bookedSlots;

    public Booking(String userLogin, int tableId, LocalDate date, int time, int period) {
        this.userLogin = userLogin;
        this.placeID = tableId;
        this.date = date;
        this.time = time;
        this.period = period;
        this.bookedSlots = new HashSet<>();
        for (int i = time; i <= time + period; i++) {
            bookedSlots.add(i);
        }
    }

    public String getUserLogin() {
        return userLogin;
    }

    public int getPlaceID() {
        return placeID;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getTime() {
        return time;
    }

    public Set<Integer> getBookedSlots() {
        return bookedSlots;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return placeID == booking.placeID
                && time == booking.time
                && period == booking.period
                && Objects.equals(userLogin, booking.userLogin)
                && Objects.equals(date, booking.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userLogin, placeID, date, time, period);
    }

    @Override
    public String toString() {
        return "Booking{" +
                ", placeID=" + placeID +
                ", date=" + date +
                ", time=" + time +
                ", period=" + period +
                ", bookedSlots=" + bookedSlots.stream().sorted().collect(Collectors.toList()) +
                '}';
    }
}
