package repository;
import java.net.URL;

public class ResourceManager {

    public static String getDefultLogoDataPath(){
        URL resource = ResourceManager.class.getClassLoader().getResource("defaultLogo.png");
        return resource.getPath();
    }
}
