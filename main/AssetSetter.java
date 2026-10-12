package main;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import object.SuperObject;
import object.OBJ_Boots;
import object.OBJ_Chest;
import object.OBJ_NextLevelDoor;
import object.OBJ_PrevLevelDoor;
import object.OBJ_Key;

public class AssetSetter {
  
  GamePanel gp;

  public AssetSetter(GamePanel gp) {
    this.gp = gp;
  }

  public void setObject(int level) {

    Arrays.fill(gp.obj, null);

    try {
        Map<String, String> data = LevelHandler.getAllLvlData(String.valueOf(level));

        int index = 0;

        for (String key : data.keySet()) {

            if (!key.endsWith("_X")) {
                continue;
            }

            String name = key.substring(0, key.length() - 2);

            String xValue = data.get(name + "_X");
            String yValue = data.get(name + "_Y");

            // Skip objects with missing coordinates
            if (xValue == null || yValue == null ||
                xValue.isBlank() || yValue.isBlank()) {
                continue;
            }

            SuperObject object = null;

            switch (name) {
                case "Key":
                    object = new OBJ_Key();
                    break;

                case "Door":
                    object = new OBJ_NextLevelDoor();
                    break;

                case "Door2":
                    object = new OBJ_PrevLevelDoor();
                    break;

                default:
                    System.out.println("Unknown object: " + name);
                    continue;
            }

            object.worldX = Integer.parseInt(xValue) * GamePanel.tileSize;
            object.worldY = Integer.parseInt(yValue) * GamePanel.tileSize;

            if (index >= gp.obj.length) {
                System.out.println("Object array is full!");
                break;
            }

            gp.obj[index] = object;
            index++;
        }

    } catch (IOException e) {
        e.printStackTrace();
    }
}
}
