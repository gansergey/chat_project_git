import java.sql.*;

public class DBAuthService implements AuthService {

    private static final String DC_CONNECTION = "jdbc:postgresql://localhost:5432/dbChat";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "admin";
    private static Connection connection;

    static {
        try {
            connection = DriverManager.getConnection(DC_CONNECTION, DB_USER, DB_PASSWORD);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        try (PreparedStatement stm = connection.prepareStatement("SELECT * FROM chat_users WHERE login = '" + login
                + "' AND pass = '" +password+"'" );
             ResultSet resultSet = stm.executeQuery();){
            if (resultSet.next()){
                return login;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
}
