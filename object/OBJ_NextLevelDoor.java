package object;

import java.io.IOException;
import javax.imageio.ImageIO;

public class OBJ_NextLevelDoor extends SuperObject {
  
  public OBJ_NextLevelDoor() {
    name = "NextLevelDoor";

    try {image = ImageIO.read(getClass().getResourceAsStream("/res/objects/door.png"));} catch (IOException e) {e.printStackTrace();}
   
    collision = true;
  }
}
