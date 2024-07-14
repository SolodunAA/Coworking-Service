package app.dao.postgresDao;

import app.config.YmlReader;
import app.dao.LoginDao;
import app.dto.RoleDto;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.*;


public class PostgresLoginDao implements LoginDao {
    private final String url;
    private final String userName;
    private final String password;

    @Autowired
    public PostgresLoginDao(YmlReader ymlReader) {
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
    public PostgresLoginDao(String url, String userName, String password) {
        this.url = url;
        this.userName = userName;
        this.password = password;
    }

    @Override
    public boolean checkIfUserExist(String login) {
        try (
                Connection connection = DriverManager.getConnection(url, userName, password);
                PreparedStatement ps = connection.prepareStatement(SQLParams.IS_USER_EXISTS)
        ) {
            ps.setString(1, login);
            try (var resultSet = ps.executeQuery()) {
                resultSet.next();
                return resultSet.getBoolean(1);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addNewUser(String login, int encodedPswd) {
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps1 = connection.prepareStatement(SQLParams.INSERT_LOGIN_AND_ROLE);
             PreparedStatement ps2 = connection.prepareStatement(SQLParams.INSERT_PASSWORD)) {
            ps1.setString(1, login);
            ps1.setString(2, "user");
            ps1.executeUpdate();
            ps2.setString(1, login);
            ps2.setInt(2, encodedPswd);
            ps2.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getEncodedPassword(String login) {
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = connection.prepareStatement(SQLParams.SELECT_ENCODED_PASSWORD)) {
            ps.setString(1, login);
            try (ResultSet resultSet = ps.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("password_encoded");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RoleDto getUserRole(String login) {
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = connection.prepareStatement(SQLParams.SELECT_USER_ROLE)) {
            ps.setString(1, login);
            try (ResultSet resultSet = ps.executeQuery()) {
                resultSet.next();
                String role = resultSet.getString("role");
                if (role.equals("admin")) {
                    role = "ADMIN";
                } else {
                    role = "USER";
                }
                return RoleDto.valueOf(role);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
