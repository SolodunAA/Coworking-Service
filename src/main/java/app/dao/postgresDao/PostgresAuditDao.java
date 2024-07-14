package app.dao.postgresDao;

import app.config.YmlReader;
import app.dao.AuditDao;
import app.dto.AuditItem;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresAuditDao implements AuditDao {
    private final String url;
    private final String userName;
    private final String password;
    @Autowired
    public PostgresAuditDao(YmlReader ymlReader) {
        this.url = ymlReader.getUrl();
        this.userName = ymlReader.getUsername();
        this.password = ymlReader.getPassword();
    }
    /**
     * constructor for testing
     * @param url
     * @param userName
     * @param password
     */
    public PostgresAuditDao(String url, String userName, String password) {
        this.url = url;
        this.userName = userName;
        this.password = password;
    }

    @Override
    public void addAuditItem(AuditItem auditItem) {
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = connection.prepareStatement(SQLParams.INSERT_AUDIT_ITEM_SQL)) {
            ps.setString(1, auditItem.getUser());
            ps.setLong(2, auditItem.getTimestamp());
            ps.setString(3, auditItem.getAction());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int AuditItemsSize() {
        int size = 0;
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = connection.prepareStatement(SQLParams.AUDIT_TABLE_SIZE)){
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                size = resultSet.getInt(1);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return size;
    }

    @Override
    public List<AuditItem> getAuditItems(int limit) {
        List<AuditItem> list = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = connection.prepareStatement(SQLParams.GET_AUDIT_RECORDS_SQL)) {
            ps.setInt(1, limit);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                String login = resultSet.getString("login");
                long timestamp = resultSet.getLong("time");
                String action = resultSet.getString("action");
                AuditItem auditItem = new AuditItem(login, timestamp, action);
                list.add(auditItem);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}
