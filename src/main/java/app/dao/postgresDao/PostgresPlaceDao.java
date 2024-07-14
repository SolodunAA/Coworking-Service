package app.dao.postgresDao;

import app.config.YmlReader;
import app.dao.PlaceDao;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

public class PostgresPlaceDao implements PlaceDao {
    private final String url;
    private final String userName;
    private final String password;
    @Autowired
    public PostgresPlaceDao(YmlReader ymlReader) {
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
    public PostgresPlaceDao(String url, String userName, String password) {
        this.url = url;
        this.userName = userName;
        this.password = password;
    }


    @Override
    public String addNewPlace(String placeName, String placeType) {
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = connection.prepareStatement(SQLParams.INSERT_PLACE)) {
            ps.setString(1, placeName);
            ps.setString(2, placeType);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "Added successfully";
    }

    @Override
    public String deletePlace(String placeName) {
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = connection.prepareStatement(SQLParams.DELETE_PLACE)) {
            ps.setString(1, placeName);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "Deleted successfully";
    }

    @Override
    public String addNewDesk(String roomName) {
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps1 = connection.prepareStatement(SQLParams.MAX_DESK_NUMBER);
             PreparedStatement ps2 = connection.prepareStatement(SQLParams.INSERT_DESK)){
            ps1.setString(1, roomName);
            try(ResultSet resultSet = ps1.executeQuery()) {
                resultSet.next();
                String nextTableNumber = resultSet.getString("max_desk_number");
                System.out.println(nextTableNumber);
                if(nextTableNumber == null){
                    nextTableNumber = "0";
                }
                ps2.setInt(1, Integer.parseInt(nextTableNumber) + 1);
                ps2.setString(2, roomName);
                ps2.executeUpdate();
            }
        } catch(Exception e){
            throw new RuntimeException(e);
        }
        return "Desk added successfully";
    }

    @Override
    public String deleteDesk(int deskNumber, String roomName) {
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = connection.prepareStatement(SQLParams.DELETE_DESK)) {
            ps.setInt(1, deskNumber);
            ps.setString(2, roomName);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "Desk deleted successfully";
    }

    @Override
    public Set<String> getAllRooms() {
        Set<String> set = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = connection.prepareStatement(SQLParams.SELECT_ROOMS)) {
            try(ResultSet resultSet = ps.executeQuery()){
                while (resultSet.next()) {
                    String placeName = resultSet.getString("place_name");
                    set.add(placeName);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return set;
    }

    @Override
    public Set<String> getAllHalls() {
        Set<String> set = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = connection.prepareStatement(SQLParams.SELECT_HALLS)) {
            try(ResultSet resultSet = ps.executeQuery()){
                while (resultSet.next()) {
                    String placeName = resultSet.getString("place_name");
                    set.add(placeName);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return set;
    }


    @Override
    public Set<Integer> getSetOfAllDesksInRoom(String roomName) {
        Set<Integer> set = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = connection.prepareStatement(SQLParams.SELECT_DESKS_FROM_ROOM)) {
            ps.setString(1, roomName);
            try(ResultSet resultSet = ps.executeQuery()){
                while (resultSet.next()) {
                    int deskNumber = resultSet.getInt("desk_number");
                    set.add(deskNumber);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return set;
    }
    @Override
    public boolean isPlaceExists(String placeName) {
        boolean exists = false;
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = connection.prepareStatement(SQLParams.IS_PLACE_EXISTS)) {
            ps.setString(1, placeName);
            try(ResultSet resultSet = ps.executeQuery()){
                while (resultSet.next()) {
                    exists = resultSet.getBoolean(1);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return exists;
    }

    @Override
    public boolean isDeskExistsInRoom(String roomName, int deskNumber) {
        boolean exists = false;
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = connection.prepareStatement(SQLParams.IS_DESK_EXISTS)) {
            ps.setString(1, roomName);
            ps.setInt(2, deskNumber);
            try(ResultSet resultSet = ps.executeQuery()){
                while (resultSet.next()) {
                    exists = resultSet.getBoolean(1);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return exists;
    }
}
