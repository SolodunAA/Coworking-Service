package app.dto.request;

import java.util.Objects;

public class HallRequest {
    private String hallName;

    public HallRequest() {
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HallRequest that = (HallRequest) o;
        return Objects.equals(hallName, that.hallName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hallName);
    }
}
