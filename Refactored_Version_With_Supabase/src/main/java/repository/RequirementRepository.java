package repository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import classes.*;

public class RequirementRepository {

    public static Requirement findRequirement(Major major, String year, String sem){
        try (BufferedReader reader = new BufferedReader(new FileReader(ResourceManager.getRequirementMapping()))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] value = line.split("\t");
                
                if(value[0].equalsIgnoreCase(major.getMajorID())){
                    for(var requirement:findRequirementByYearandSem(year, sem)){
                        if(requirement.getRequirementID().equals(value[1])){
                            return requirement;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Requirement> findRequirementByYearandSem(String year, String sem){
        List<Requirement> requirements = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ResourceManager.getRequirement()))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] value = line.split("\t");
                if(value[1].equalsIgnoreCase(year) && value[2].equalsIgnoreCase(sem) ){
                    Requirement requirement = new Requirement(value[0], value[1], value[2], Integer.parseInt(value[3]), Integer.parseInt(value[4]));
                    requirements.add(requirement);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return requirements;
    }
}
