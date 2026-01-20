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
    private static User currentUser = null;
    private static final String userfile = "src/main/data/user_account.txt";
    private static final SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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

    public boolean login(String username, String password) {
        if (this.username.equals(username) && this.password.equals(password)) {
            lastLogin = new Date();
            currentUser = this;
            timestampLog(lastLogin, "login");
            updateUserFile();
            return true;
        }
        return false;
    }

    public void logout() {
        if (currentUser != null) {
            lastLogout = new Date();
            timestampLog(lastLogout, "logout");
            updateUserFile();
            currentUser = null;
        }
    }

    public void openHomePage() {
        if (role.equals("Admin")) {
            new CourseAdminPage(this).setVisible(true);
        } else if (role.equals("AcademicOfficer")) {
            new AcademicOfficerPage(this).setVisible(true);
        }
    }

    public static List<User> loadData(String filePath) {
        List<User> users = new ArrayList<>();
        File file = new File(filePath);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (isHeader){
                    isHeader = false;
                    continue;
                }

                if (line.trim().isEmpty()) continue;
                String[] value = line.split("\t", -1);

                String username = value[0];
                String password = value[1];
                String firstName = value[2];
                String lastName = value[3];
                String email = value[4];
                String role = value[5];

                Date lastLogin = null;
                Date lastLogout = null;

                try {
                    if (!value[6].equals("null")){
                        lastLogin = new Date(Long.parseLong(value[6]));
                    }
                } catch (NumberFormatException e){
                    System.err.println("Error parsing last login");
                }

                try {
                    if (!value[7].equals("null")){
                        lastLogout = new Date(Long.parseLong(value[7]));
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing last logout");
                }

                users.add(new User(username, password, firstName, lastName, email, role, lastLogin, lastLogout));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void updateUserFile() {
        List<User> users = loadData(userfile);

        for (User user : users) {
            if (user.getUsername().equals(this.username)) {
                user.password = this.password;
                user.firstName = this.firstName;
                user.lastName = this.lastName;
                user.email = this.email;
                user.role = this.role;

                if (this.lastLogin != null) {
                    user.lastLogin = this.lastLogin;
                }
                if (this.lastLogout != null) {
                    user.lastLogout = this.lastLogout;
                }
                break;
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userfile))) {
            // Write header
            writer.write("Username\tPassword\tFirstName\tLastname\tEmail\tRole\tLastLogin\tLastLogout");
            writer.newLine();

            // Write each user
            for (User user : users) {
                // Store timestamps as binary (milliseconds since epoch)
                String lastLogin = user.lastLogin != null ? String.valueOf(user.lastLogin.getTime()) : "null";
                String lastLogout = user.lastLogout != null ? String.valueOf(user.lastLogout.getTime()) : "null";

                writer.write(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s",
                        user.username,
                        user.password,
                        user.firstName,
                        user.lastName,
                        user.email,
                        user.role,
                        lastLogin,
                        lastLogout
                ));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error updating user file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void resetPass(String username, String email, String newPass) {
        if (this.username.equals(username) && this.email.equals(email)) {
            this.password = newPass;
            updateUserFile();
        }
    }

    private void timestampLog(Date timestamp, String action) {
        String timestamp_str = dataFormat.format(timestamp);
        String log = String.format(timestamp_str, this.username, this.firstName, this.lastName, action);
        System.out.println(log);
    }

    public String getLastLoginFormatted(){
        return lastLogin != null ? dataFormat.format(lastLogin) : "Never logged in";
    }

    public String getLastLogoutFormatted() {
        return lastLogout != null ? dataFormat.format(lastLogout) : "Never logged in";
    }

    public static String getUserFilePath(){
        return userfile;
    }
}