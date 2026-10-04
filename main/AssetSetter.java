package main;

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
    for (int i = 0; i <gp.obj.length; i++) {}
    try {
    gp.obj[0] = new OBJ_NextLevelDoor();
    gp.obj[0].worldX = Integer.valueOf(main.LevelHandler.getLvlData(String.valueOf(level), "Door_X")) *GamePanel.tileSize;
    gp.obj[0].worldY = Integer.valueOf(main.LevelHandler.getLvlData(String.valueOf(level), "Door_Y")) *GamePanel.tileSize;

    gp.obj[1] = new OBJ_Key();
    gp.obj[1].worldX = Integer.valueOf(main.LevelHandler.getLvlData(String.valueOf(level), "Key_X")) *GamePanel.tileSize;
    gp.obj[1].worldY = Integer.valueOf(main.LevelHandler.getLvlData(String.valueOf(level), "Key_Y")) *GamePanel.tileSize;

    gp.obj[2] = new OBJ_PrevLevelDoor();
    gp.obj[2].worldX = Integer.valueOf(main.LevelHandler.getLvlData(String.valueOf(level), "Door2_X")) *GamePanel.tileSize;
    gp.obj[2].worldY = Integer.valueOf(main.LevelHandler.getLvlData(String.valueOf(level), "Door2_Y")) *GamePanel.tileSize;
    } catch (Exception e) {}
  }
}
