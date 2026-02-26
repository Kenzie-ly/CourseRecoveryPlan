package repository;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseManager {
    private static final String URL = "jdbc:postgresql://db.zcolpwxxdlhgawzrwkxm.supabase.co:5432/postgres?";
    private static final String USER =  "postgres";
    private static final String PASSWORD = "#Kenletken123";

    public static Connection getConnection() throws Exception{
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
