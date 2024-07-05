package app.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;

import static java.time.ZoneOffset.UTC;

public class AuditItem {
    private final String user;
    private final long timestamp;
    private final String action;

    public AuditItem(String user, String action) {
        this.user = user;
        this.timestamp = System.currentTimeMillis();
        this.action = action;
    }

    public AuditItem(String user, long timestamp, String action) {
        this.user = user;
        this.timestamp = timestamp;
        this.action = action;
    }

    public String getUser() {
        return user;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getAction() {
        return action;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditItem auditItem = (AuditItem) o;
        return timestamp == auditItem.timestamp && Objects.equals(user, auditItem.user) && Objects.equals(action, auditItem.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, timestamp, action);
    }

    @Override
    public String toString() {
        return "AuditItem{" +
                "user='" + user + '\'' +
                ", timestamp=" + timestamp +
                ", action='" + action + '\'' +
                '}';
    }
}