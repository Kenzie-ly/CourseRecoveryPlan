package repository;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseManager {
    //Java Database Connectivity
    private static final String URL = "jdbc:postgresql://aws-1-ap-south-1.pooler.supabase.com:6543/postgres?";
    private static final String USER =  "postgres.zcolpwxxdlhgawzrwkxm";
    private static final String PASSWORD = "#Kenletken123";

    public static Connection getConnection() throws Exception{
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
