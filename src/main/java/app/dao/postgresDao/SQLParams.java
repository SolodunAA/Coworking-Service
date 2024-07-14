package app.dao.postgresDao;

public class SQLParams {
    public static final String INSERT_LOGIN_AND_ROLE = """
            INSERT INTO admin.user_table
            (user_login, role)
            VALUES (?, ?)
            """;


    public static final String INSERT_PASSWORD = """
            INSERT INTO admin.password_table
            (user_id, password_encoded)
            VALUES ((SELECT user_id FROM admin.user_table WHERE user_login = ?), ?);
            """;

    public static final String IS_USER_EXISTS = """
            SELECT EXISTS (
            SELECT user_login
            FROM admin.user_table
            WHERE user_login = ?
            );
            """;

    public static final String SELECT_ENCODED_PASSWORD = """
            SELECT password_encoded FROM admin.password_table
            WHERE user_id = (SELECT user_id FROM admin.user_table WHERE user_login = ?)
            """;
    public static final String INSERT_PLACE = """
            INSERT INTO coworking.place_table
            (place_name, place_type)
            VALUES (?, ?);
            """;
    public static final String INSERT_DESK = """
            INSERT INTO coworking.desk_table
            (desk_number, place_id)
            VALUES (?, (SELECT place_id FROM coworking.place_table WHERE place_name = ?));
            """;
    public static final String MAX_DESK_NUMBER = """
            SELECT MAX(desk_number) AS max_desk_number
            FROM coworking.desk_table
            WHERE place_id = (SELECT place_id FROM coworking.place_table WHERE place_name = ?);
            """;

    public static final String DELETE_PLACE = """
            DELETE FROM coworking.place_table
            WHERE place_name = ?;
            """;

    public static final String DELETE_DESK = """
            DELETE FROM coworking.desk_table
            WHERE desk_number = ? AND place_id = (SELECT place_id FROM coworking.place_table WHERE place_name = ?);
            """;

    public static final String SELECT_HALLS = """
            SELECT place_name
            FROM coworking.place_table
            WHERE place_type = 'hall';
            """;
    public static final String SELECT_ROOMS = """
            SELECT place_name
            FROM coworking.place_table
            WHERE place_type = 'room';
            """;

    public static final String SELECT_DESKS_FROM_ROOM = """
            SELECT desk_number
            FROM coworking.place_table JOIN coworking.desk_table USING(place_id)
            WHERE place_name = ?;
            """;

    public static final String INSERT_DESK_BOOKING = """
            INSERT INTO coworking.booking_table
            (user_id, place_id, desk_id, date, start_time, end_time)
            VALUES (
            (SELECT user_id FROM admin.user_table WHERE user_login = ?),
            (SELECT place_id FROM coworking.place_table WHERE place_name = ?),
            (SELECT desk_id FROM coworking.place_table JOIN coworking.desk_table USING(place_id) WHERE place_name = ? AND desk_number = ?),
            ?, ?, ?)
            """;

    public static final String INSERT_HALL_BOOKING = """
            INSERT INTO coworking.booking_table
            (user_id, place_id, desk_id, date, start_time, end_time)
            VALUES (
            (SELECT user_id FROM admin.user_table WHERE user_login = ?),
            (SELECT place_id FROM coworking.place_table WHERE place_name = ?),
            null, ?, ?, ?)
            """;

    public static final String DELETE_BOOKING = """
            DELETE FROM coworking.booking_table
            WHERE booking_id = ?;
            """;

    public static final String SELECT_USER_BOOKINGS = """
            SELECT booking_id, place_name, desk_number, date, start_time, end_time
            FROM coworking.booking_table
            JOIN admin.user_table USING(user_id)
            JOIN coworking.place_table USING(place_id)
            LEFT JOIN coworking.desk_table USING(desk_id)
            WHERE user_login = ?;
            """;

    public static final String SELECT_USER_LOGINS_WHO_BOOKED = """
            SELECT user_login
            FROM coworking.booking_table JOIN admin.user_table USING(user_id);
            """;

    public static final String UPDATE_BOOKING_DATE = """
            UPDATE coworking.booking_table SET date = ?, start_time = ?, end_time = ?
            WHERE booking_id = ?;
            """;
    public static final String UPDATE_BOOKING_TIME = """
            UPDATE coworking.booking_table SET start_time = ?, end_time = ?
            WHERE booking_id = ?;
            """;

    public static final String SELECT_USER_ROLE = """
            SELECT role FROM admin.user_table
            WHERE user_login = ?
            """;
    public static final String SELECT_BOOKING_BY_ID = """
            SELECT booking_id, user_login, place_name, desk_number, date, start_time, end_time
            FROM coworking.booking_table
            JOIN admin.user_table USING(user_id)
            JOIN coworking.place_table USING(place_id)
            LEFT JOIN coworking.desk_table USING(desk_id)
            WHERE booking_id = ?;
            """;
    public static final String SELECT_SLOTS_FOR_DESKS_ON_DATE = """
            SELECT desk_number, start_time, end_time
            FROM coworking.booking_table JOIN coworking.desk_table USING(desk_id)
            WHERE coworking.desk_table.place_id = (
            SELECT place_id FROM coworking.place_table WHERE place_name = ?)
            AND date = ?;
            """;
    public static final String SELECT_SLOTS_FOR_HALLS_ON_DATE = """
            SELECT place_name, start_time, end_time
            FROM coworking.booking_table JOIN coworking.place_table USING(place_id)
            WHERE place_type = 'hall' AND date = ?;
            """;

    public static final String IS_PLACE_EXISTS = """
            SELECT EXISTS (
            SELECT place_name
            FROM coworking.place_table
            WHERE place_name = ?
            );
            """;
    public static final String IS_DESK_EXISTS = """
            SELECT EXISTS (
            SELECT desk_number
            FROM coworking.place_table
            JOIN coworking.desk_table USING(place_id)
            WHERE place_name = ? AND desk_number = ?
            );
            """;

    public static final String SELECT_ALL_BOOKINGS = """
            SELECT *
            FROM coworking.booking_table
            JOIN admin.user_table USING(user_id)
            JOIN coworking.place_table USING(place_id)
            LEFT JOIN coworking.desk_table USING(desk_id)
            ORDER BY date;
            """;

    public static final String IS_USER_HAVE_BOOKING_ID = """
            SELECT EXISTS (
            SELECT booking_id
            FROM coworking.booking_table
            JOIN admin.user_table USING (user_id)
            WHERE user_login = ? AND booking_id = ?
            );
            """;
    public static final String INSERT_AUDIT_ITEM_SQL = """
            INSERT INTO admin.audit_table
            (login, time, action)
            VALUES
            (?, ?, ?);
            """;

    public static final String GET_AUDIT_RECORDS_SQL = """
            SELECT login, time, action
            FROM admin.audit_table
            LIMIT ?
            """;
    public static final String AUDIT_TABLE_SIZE = """
            SELECT COUNT(*) FROM admin.audit_table;
            """;
}
