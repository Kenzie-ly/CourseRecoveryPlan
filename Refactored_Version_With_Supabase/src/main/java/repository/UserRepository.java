package repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import classes.*;

public class UserRepository {

    public static void createUser(User newUser) throws IOException{
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(ResourceManager.getUserDataPath(),true))) {
            bufferedWriter.write(formatUserToString(newUser));
            bufferedWriter.newLine();
        }
    }

    public static void deleteUser(User modifiedUser){
        File originalFile = new File(ResourceManager.getUserDataPath());
        File tempFile = new File("temp.txt");

        try (
            BufferedReader reader = new BufferedReader(new FileReader(originalFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\t");
                
                // CHECK: Is this the user we are updating?
                if (!(data[0].equals(modifiedUser.getUsername()))) {
                    // NO: Just copy the existing line 
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // SWAP: Delete old file, rename temp file to "users.txt"
        originalFile.delete();
        tempFile.renameTo(originalFile);
    }

    public static User findUserByUsername(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(ResourceManager.getUserDataPath()))) {
            reader.readLine();//for skipping the header
            String line;
            while ((line = reader.readLine()) != null){
                String[] userData = line.split("\t");
                if (userData[0].equals(username)){
                    User user = new User(
                    userData[0], userData[1], 
                    userData[2], userData[3], 
                    userData[4], userData[5], 
                    new Date(Long.parseLong(userData[6].trim())), new Date(Long.parseLong(userData[7].trim())));
                    
                    return user;
                }
                
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public static List<User> loadAllUsers(){
        List<User> users = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(ResourceManager.getUserDataPath()))) {
            reader.readLine();//for skipping the header
            String line;
            while ((line = reader.readLine()) != null){
                String[] userData = line.split("\t");
                User user = new User(
                    userData[0], userData[1], 
                    userData[2], userData[3], 
                    userData[4], userData[5], 
                    new Date(Long.parseLong(userData[6].trim())), 
                    new Date(Long.parseLong(userData[7].trim()))
                );
                users.add(user);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.print(users);
        return users;
    }

    public static List<User> loadUserBasedOnRole(String role){
        List<User> users = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(ResourceManager.getUserDataPath()))) {
            reader.readLine();//for skipping the header
            String line;
            while ((line = reader.readLine()) != null){
                String[] userData = line.split("\t");
                if (userData[5].equalsIgnoreCase(role)){
                    User user = new User(
                    userData[0], userData[1], 
                    userData[2], userData[3], 
                    userData[4], userData[5], 
                    new Date(Long.parseLong(userData[6].trim())), new Date(Long.parseLong(userData[7].trim())));
                    
                    users.add(user);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println(users);
        return users.isEmpty() ? null:users;
    }

    public static void updateUser(User modifiedUser) {
        File originalFile = new File(ResourceManager.getUserDataPath());
        File tempFile = new File("temp.txt");

        try (
            BufferedReader reader = new BufferedReader(new FileReader(originalFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\t");
                
                // CHECK: Is this the user we are updating?
                if (data[0].equals(modifiedUser.getUsername())) {
                    
                    // YES: Write the NEW data instead of the old line
                    writer.write(formatUserToString(modifiedUser));
                    
                } else {
                    
                    // NO: Just copy the existing line exactly as is
                    writer.write(line);
                }
                writer.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // SWAP: Delete old file, rename temp file to "users.txt"
        originalFile.delete();
        tempFile.renameTo(originalFile);
    }

    private static String formatUserToString(User user){
        return user.getUsername() + "\t" +
           user.getPassword() + "\t" +
           user.getFirstName() + "\t" +
           user.getLastName() + "\t" +
           user.getEmail() + "\t" +
           user.getRole() + "\t" +
           user.getLastLogin().getTime() + "\t" +
           user.getLastLogout().getTime();
    }
}
