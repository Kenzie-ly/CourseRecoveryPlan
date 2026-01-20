import java.util.Date;

public class AcademicOfficer extends User {
    public AcademicOfficer(String username, String password, String firstName, String lastname, String email, String role, Date lastLogin, Date lastLogout) {
        super(username, password, firstName, lastname, email, role, lastLogin, lastLogout);
    }
}
