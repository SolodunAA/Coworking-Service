package app.dao.postgresDao;

import app.dao.LoginDao;
import app.dto.Role;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class PostgresLoginDao implements LoginDao {
    private final String url;
    private final String userName;
    private final String password;

    public PostgresLoginDao(String url, String userName, String password) {
        this.url = url;
        this.userName = userName;
        this.password = password;
    }
    @Override
    public boolean checkIfUserExist(String login) {
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = connection.prepareStatement(SQLParams.CHECK_USER_EXISTS)) {
            ps.setString(1, login);
            ResultSet resultSet = ps.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) > 0;
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
            ResultSet resultSet = ps.executeQuery();
            resultSet.next();
            return resultSet.getInt("password_encoded");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Role getUserRole(String login) {
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = connection.prepareStatement(SQLParams.SELECT_USER_ROLE)) {
            ps.setString(1, login);
            ResultSet resultSet = ps.executeQuery();
            resultSet.next();
            String role = resultSet.getString("role");
            if(role.equals("admin")){
                role = "ADMIN";
            } else {
                role = "USER";
            }
            return Role.valueOf(role);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
