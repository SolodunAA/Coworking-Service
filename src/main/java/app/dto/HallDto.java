package app.dto;

import java.util.Objects;

public class HallDto {
    private String hallName;

        public HallDto() {
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
        HallDto hallDto = (HallDto) o;
        return Objects.equals(hallName, hallDto.hallName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hallName);
    }
}
