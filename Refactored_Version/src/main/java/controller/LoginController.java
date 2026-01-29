package controller;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import classes.*;
import repository.UserRepository;

public class LoginController {
    
    private User user = null;
    private List<User> currentDeletedUsers = new ArrayList<>();

    public User getUser(){
        return user;
    }

    public User addNewUser(User user){
        try{
            UserRepository.createUser(user);
            return user;
        }catch (Exception e){
            System.out.println(e);
            return null;
        }
    }

    public void updateUserDetails(User modifiedUser) {
        UserRepository.updateUser(modifiedUser);
    }

    public User removeUser(User userToDelete) {
        currentDeletedUsers.add(userToDelete);
        UserRepository.deleteUser(userToDelete);

        return userToDelete;
    }
    
    public List<User> getAllUsers(){
        return UserRepository.loadAllUsers();
    }

    public List<User> getAdminUsers(){
        return UserRepository.loadUserBasedOnRole("admin");
    }

    public List<User> getOfficUsers(){
        return UserRepository.loadUserBasedOnRole("AcademicOfficer");
    }

    public User authenticateUser(User tempUser){
        user = UserRepository.findUserByUsername(tempUser.getUsername());
        if(user != null &&  user.getPassword().equals(tempUser.getPassword())){
            user.setLastLogin(new Date());
            return user;
        }
        return null;
    }

    // Get Email Using Username And Send Verification Code
    public User emailRetrieval(String username, String verifCode) {
        user = UserRepository.findUserByUsername(username);

        String email = user.getEmail();
        String bodyText = "This is your verification code: " + verifCode;
        EmailSender.sendEmail(email, "Forget Password Verification Code", bodyText);
        return user;
        
    }

    public boolean checkEmail(String username, String conNewPass, String email){
        if (user.getUsername().equals(username) && user.getEmail().equals(email)) {
            user.setPassword(conNewPass);
            UserRepository.updateUser(user);
            return true;
        }
        return false;
    }

    public void logout() {
        user.setLastLogout(new Date());   
    }
}
