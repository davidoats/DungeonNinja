package main;

import entity.Entity;
import object.SuperObject;

public class CollisionChecker {

    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
      this.gp = gp;
    }

    public void checkTile(Entity entity) {
        int entityLeftWorldX = (int)entity.WorldX + entity.solidArea.x;
        int entityRightWorldX = (int)entity.WorldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = (int)entity.WorldY + entity.solidArea.y;
        int entityBottomWorldY = (int)entity.WorldY + entity.solidArea.y + entity.solidArea.height;

        int entityLeftCol = entityLeftWorldX/GamePanel.tileSize;
        int entityRightCol = entityRightWorldX/GamePanel.tileSize;
        int entityTopRow = entityTopWorldY/GamePanel.tileSize;
        int entityBottomRow = entityBottomWorldY/GamePanel.tileSize;

        int tileNum1, tileNum2;

        switch(entity.direction) {

    case "up":
        entityTopRow =
                (int)(entityTopWorldY - entity.speed)
                / GamePanel.tileSize;

        if (entityTopRow < 0 ||
            entityLeftCol < 0 ||
            entityRightCol >= gp.maxWorldCol) {

            entity.collisionOn = true;
            break;
        }

        tileNum1 =
                gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
        tileNum2 =
                gp.tileM.mapTileNum[entityRightCol][entityTopRow];

        if (gp.tileM.tile[tileNum1].collision ||
            gp.tileM.tile[tileNum2].collision) {

            entity.collisionOn = true;
        }

        break;


    case "down":
        entityBottomRow =
                (int)(entityBottomWorldY + entity.speed)
                / GamePanel.tileSize;

        if (entityBottomRow >= gp.maxWorldRow ||
            entityLeftCol < 0 ||
            entityRightCol >= gp.maxWorldCol) {

            entity.collisionOn = true;
            break;
        }

        tileNum1 =
                gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
        tileNum2 =
                gp.tileM.mapTileNum[entityRightCol][entityBottomRow];

        if (gp.tileM.tile[tileNum1].collision ||
            gp.tileM.tile[tileNum2].collision) {

            entity.collisionOn = true;
        }

        break;


    case "left":
        entityLeftCol =
                (int)(entityLeftWorldX - entity.speed)
                / GamePanel.tileSize;

        if (entityLeftCol < 0 ||
            entityTopRow < 0 ||
            entityBottomRow >= gp.maxWorldRow) {

            entity.collisionOn = true;
            break;
        }

        tileNum1 =
                gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
        tileNum2 =
                gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];

        if (gp.tileM.tile[tileNum1].collision ||
            gp.tileM.tile[tileNum2].collision) {

            entity.collisionOn = true;
        }

        break;


    case "right":
        entityRightCol =
                (int)(entityRightWorldX + entity.speed)
                / GamePanel.tileSize;

        if (entityRightCol >= gp.maxWorldCol ||
            entityTopRow < 0 ||
            entityBottomRow >= gp.maxWorldRow) {

            entity.collisionOn = true;
            break;
        }

        tileNum1 =
                gp.tileM.mapTileNum[entityRightCol][entityTopRow];
        tileNum2 =
                gp.tileM.mapTileNum[entityRightCol][entityBottomRow];

        if (gp.tileM.tile[tileNum1].collision ||
            gp.tileM.tile[tileNum2].collision) {

            entity.collisionOn = true;
        }

        break;
}
    }
public SuperObject checkObject(Entity entity, boolean player) {

    synchronized (gp.objLock) {

        SuperObject collidedObject = null;

      for (int i = 0; i < gp.obj.size(); i++) {
    if (gp.obj.get(i) != null) {
          //get entity solid area pos.
          entity.solidArea.x = (int)entity.WorldX + entity.solidArea.x;
          entity.solidArea.y = (int)entity.WorldY + entity.solidArea.y;

          //get obj solid area position
          gp.obj.get(i).solidArea.x = (int)gp.obj.get(i).worldX + gp.obj.get(i).solidArea.x;
          gp.obj.get(i).solidArea.y = (int)gp.obj.get(i).worldY + gp.obj.get(i).solidArea.y;

          switch (entity.direction) {
            case "up":
              entity.solidArea.y -= entity.speed;
              if(entity.solidArea.intersects(gp.obj.get(i).solidArea)) {
                if (gp.obj.get(i).collision == true) {
                  entity.collisionOn = true;
                }
                if(player) {
                  collidedObject = gp.obj.get(i);
                }
              }
              break;
            case "down":
              entity.solidArea.y += entity.speed;
              if(entity.solidArea.intersects(gp.obj.get(i).solidArea)) {
                if (gp.obj.get(i).collision == true) {
                  entity.collisionOn = true;
                }
                if(player) {
                  collidedObject = gp.obj.get(i);
                }
              }
              break;
            case "left":
              entity.solidArea.x -= entity.speed;
              if(entity.solidArea.intersects(gp.obj.get(i).solidArea)) {
                if (gp.obj.get(i).collision == true) {
                  entity.collisionOn = true;
                }
                if(player) {
                  collidedObject = gp.obj.get(i);
                }
              }
              break;
            case "right":
              entity.solidArea.x += entity.speed;
              if(entity.solidArea.intersects(gp.obj.get(i).solidArea)) {
                  if (gp.obj.get(i).collision == true) {
                    entity.collisionOn = true;
                  }
                  if(player) {
                    collidedObject = gp.obj.get(i);
                  }
              }
              break;
          }

          entity.solidArea.x = entity.solidAreaDefaultX;
          entity.solidArea.y = entity.solidAreaDefaultY;
          gp.obj.get(i).solidArea.x = gp.obj.get(i).solidAreaDefaultX;
          gp.obj.get(i).solidArea.y = gp.obj.get(i).solidAreaDefaultY;
        }
      }

      return collidedObject;
    }

    
}
}
    
