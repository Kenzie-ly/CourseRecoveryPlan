import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class User {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private Date lastLogin;
    private Date lastLogout;
    private static final SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, String firstName, String lastName, String email, String role, Date lastLogin, Date lastLogout){
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.lastLogin = lastLogin;
        this.lastLogout = lastLogout;
    }

    public void setPassword(String password) { this.password = password; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }
    public void setLastLogin(Date lastLogin) { this.lastLogin = lastLogin; }
    public void setLastLogout(Date lastLogout) { this.lastLogout = lastLogout; }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public Date getLastLogin() { return lastLogin; }
    public Date getLastLogout() { return lastLogout; }


    public String getLastLoginFormatted(){
        return lastLogin != null ? dataFormat.format(lastLogin) : "Never logged in";
    }

    public String getLastLogoutFormatted() {
        return lastLogout != null ? dataFormat.format(lastLogout) : "Never logged in";
    }
}