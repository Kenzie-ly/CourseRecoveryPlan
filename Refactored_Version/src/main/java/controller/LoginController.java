package controller;
import java.util.Date;

import classes.*;
import repository.UserRepository;

public class LoginController {
    
    private User user = null;

    public User getLoggedInUser() {
        if (user != null){
            return this.user;
        }
        return null;
    }

    public boolean authenticateUser(User tempUser){
        user = UserRepository.findUserByUsername(tempUser.getUsername());
        if(user != null &&  user.getPassword().equals(tempUser.getPassword())){
            user.setLastLogin(new Date());
            return true;
        }
        return false;
    }

    public void logout() {
        user.setLastLogout(new Date());   
    }
}
