package repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import classes.*;

public class UserRepository {

    public static void createUser(User newUser) throws Exception{
        String insertString = """
                    INSERT INTO user_accounts (Username, Password, FirstName, LastName, Email, Role, LastLogin, LastLogout)
                    VALUES
                    (?,?,?,?,?,?,?,?)
                """;

        Connection conn = DatabaseManager.getConnection();
        PreparedStatement statement = conn.prepareStatement(insertString);

        statement.setString(1, newUser.getUsername());
        statement.setString(2, newUser.getPassword());
        statement.setString(3, newUser.getFirstName());
        statement.setString(4, newUser.getLastName());
        statement.setString(5, newUser.getEmail());
        statement.setString(6, newUser.getRole());
        statement.setLong(7, newUser.getLastLogin().getTime());
        statement.setLong(8, newUser.getLastLogout().getTime());
        statement.executeUpdate();
    }

    public static void deleteUser(User modifiedUser){
        String deleteString = "DELETE FROM user_accounts WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement statement = conn.prepareStatement(deleteString)){
            statement.setString(1, modifiedUser.getUsername());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public static User findUserByUsername(String username) {
        String findString = "SELECT * FROM user_accounts where username = ?";
        try(Connection conn = DatabaseManager.getConnection(); PreparedStatement statement = conn.prepareStatement(findString)){
            statement.setString(1, username);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                User user = new User(
                    rs.getString("username"), 
                    rs.getString("password"), 
                    rs.getString("firstname"), 
                    rs.getString("lastname"), 
                    rs.getString("email"), 
                    rs.getString("role"), 
                    new Date(Long.parseLong(rs.getString("lastlogin"))),
                    new Date(Long.parseLong(rs.getString("lastlogout")))
                );
                return user;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static List<User> loadAllUsers(){
        List<User> users = new ArrayList<>();
        String loadString = "Select * FROM user_accounts";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement statement = conn.prepareStatement(loadString)) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                users.add(
                    new User(
                        rs.getString("username"), 
                        rs.getString("password"), 
                        rs.getString("firstname"), 
                        rs.getString("lastname"), 
                        rs.getString("email"), 
                        rs.getString("role"), 
                        new Date(Long.parseLong(rs.getString("lastlogin"))),
                        new Date(Long.parseLong(rs.getString("lastlogout")))
                    )
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public static List<User> loadUserBasedOnRole(String role){
        List<User> users = new ArrayList<>();

        String loadString = "Select * FROM user_accounts WHERE role = ?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement statement = conn.prepareStatement(loadString)) {
            statement.setString(1, role);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                users.add(
                    new User(
                        rs.getString("username"), 
                        rs.getString("password"), 
                        rs.getString("firstname"), 
                        rs.getString("lastname"), 
                        rs.getString("email"), 
                        role, 
                        new Date(Long.parseLong(rs.getString("lastlogin"))),
                        new Date(Long.parseLong(rs.getString("lastlogout")))
                    )
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users.isEmpty() ? null:users;
    }

    public static void updateUser(User modifiedUser) {
        String updateString = """
                    UPDATE user_accounts 
                    SET
                        password = ?, 
                        firstname = ?,
                        lastname = ?,
                        email = ?,
                        role = ?,
                        lastlogin = ?,
                        lastlogout = ?                  
                    WHERE username = ?
                """;

        try(Connection conn = DatabaseManager.getConnection(); PreparedStatement statement = conn.prepareStatement(updateString)){
            statement.setString(1, modifiedUser.getPassword());
            statement.setString(2, modifiedUser.getFirstName());
            statement.setString(3, modifiedUser.getLastName());
            statement.setString(4, modifiedUser.getEmail());
            statement.setString(5, modifiedUser.getRole());
            statement.setLong(6, modifiedUser.getLastLogin().getTime());
            statement.setLong(7, modifiedUser.getLastLogout().getTime());
            statement.setString(8, modifiedUser.getUsername());
            statement.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
