package main;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
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

        // Remove objects from the previous level
        synchronized (gp.objLock) {
            gp.obj.clear();
        }

        try {
            JsonArray objects = LevelHandler.getObjects(String.valueOf(level));

            for (JsonElement element : objects) {

                JsonObject data = element.getAsJsonObject();

                String type = data.get("type").getAsString();
                double x = data.get("x").getAsDouble();
                double y = data.get("y").getAsDouble();

                SuperObject object;

                switch (type) {
                    case "Key":
                        object = new OBJ_Key();
                        break;

                    case "NextLevelDoor":
                        object = new OBJ_NextLevelDoor();
                        break;

                    case "PrevLevelDoor":
                        object = new OBJ_PrevLevelDoor();
                        break;

                    case "Boots":
                        object = new OBJ_Boots();
                        break;

                    case "Chest":
                        object = new OBJ_Chest();
                        break;

                    default:
                        System.out.println("Unknown object: " + type);
                        continue;
                }

                // Position object
                object.worldX = x * GamePanel.tileSize;
                object.worldY = y * GamePanel.tileSize;

                // Add object to dynamic list
                synchronized (gp.objLock) {
                    gp.obj.add(object);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}