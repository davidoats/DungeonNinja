package object;

import java.io.IOException;
import javax.imageio.ImageIO;

public class OBJ_PrevLevelDoor extends SuperObject {
  
  public OBJ_PrevLevelDoor() {
    name = "PrevLevelDoor";

    try {image = ImageIO.read(getClass().getResourceAsStream("/res/objects/door.png"));} catch (IOException e) {e.printStackTrace();}
   
    collision = true;
  }
}
