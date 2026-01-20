import javax.swing.*;

public class CRS {
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            LoginPage loginPage = new LoginPage();
            loginPage.setVisible(true);
        });
    }
}