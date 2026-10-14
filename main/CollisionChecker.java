package main;

import entity.*;
import object.SuperObject;
import java.awt.geom.Rectangle2D;

public class CollisionChecker {

  GamePanel gp;

  public CollisionChecker(GamePanel gp) {
    this.gp = gp;
  }

  public void checkTile(Entity entity) {
    int entityLeftWorldX = (int) entity.WorldX + entity.solidArea.x;
    int entityRightWorldX = (int) entity.WorldX + entity.solidArea.x + entity.solidArea.width;
    int entityTopWorldY = (int) entity.WorldY + entity.solidArea.y;
    int entityBottomWorldY = (int) entity.WorldY + entity.solidArea.y + entity.solidArea.height;

    int entityLeftCol = entityLeftWorldX / GamePanel.tileSize;
    int entityRightCol = entityRightWorldX / GamePanel.tileSize;
    int entityTopRow = entityTopWorldY / GamePanel.tileSize;
    int entityBottomRow = entityBottomWorldY / GamePanel.tileSize;

    int tileNum1, tileNum2;

    switch (entity.direction) {

      case "up":
        entityTopRow = (int) (entityTopWorldY - entity.speed)
            / GamePanel.tileSize;

        if (entityTopRow < 0 ||
            entityLeftCol < 0 ||
            entityRightCol >= gp.maxWorldCol) {

          entity.collisionOn = true;
          break;
        }

        tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
        tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];

        if (gp.tileM.tile[tileNum1].collision ||
            gp.tileM.tile[tileNum2].collision) {

          entity.collisionOn = true;
        }

        break;

      case "down":
        entityBottomRow = (int) (entityBottomWorldY + entity.speed)
            / GamePanel.tileSize;

        if (entityBottomRow >= gp.maxWorldRow ||
            entityLeftCol < 0 ||
            entityRightCol >= gp.maxWorldCol) {

          entity.collisionOn = true;
          break;
        }

        tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
        tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];

        if (gp.tileM.tile[tileNum1].collision ||
            gp.tileM.tile[tileNum2].collision) {

          entity.collisionOn = true;
        }

        break;

      case "left":
        entityLeftCol = (int) (entityLeftWorldX - entity.speed)
            / GamePanel.tileSize;

        if (entityLeftCol < 0 ||
            entityTopRow < 0 ||
            entityBottomRow >= gp.maxWorldRow) {

          entity.collisionOn = true;
          break;
        }

        tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
        tileNum2 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];

        if (gp.tileM.tile[tileNum1].collision ||
            gp.tileM.tile[tileNum2].collision) {

          entity.collisionOn = true;
        }

        break;

      case "right":
        entityRightCol = (int) (entityRightWorldX + entity.speed)
            / GamePanel.tileSize;

        if (entityRightCol >= gp.maxWorldCol ||
            entityTopRow < 0 ||
            entityBottomRow >= gp.maxWorldRow) {

          entity.collisionOn = true;
          break;
        }

        tileNum1 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
        tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];

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
          // get entity solid area pos.
          entity.solidArea.x = (int) entity.WorldX + entity.solidArea.x;
          entity.solidArea.y = (int) entity.WorldY + entity.solidArea.y;

          // get obj solid area position
          gp.obj.get(i).solidArea.x = (int) gp.obj.get(i).worldX + gp.obj.get(i).solidArea.x;
          gp.obj.get(i).solidArea.y = (int) gp.obj.get(i).worldY + gp.obj.get(i).solidArea.y;

          switch (entity.direction) {
            case "up":
              entity.solidArea.y -= entity.speed;
              if (entity.solidArea.intersects(gp.obj.get(i).solidArea)) {
                if (gp.obj.get(i).collision == true) {
                  entity.collisionOn = true;
                }
                if (player) {
                  collidedObject = gp.obj.get(i);
                }
              }
              break;
            case "down":
              entity.solidArea.y += entity.speed;
              if (entity.solidArea.intersects(gp.obj.get(i).solidArea)) {
                if (gp.obj.get(i).collision == true) {
                  entity.collisionOn = true;
                }
                if (player) {
                  collidedObject = gp.obj.get(i);
                }
              }
              break;
            case "left":
              entity.solidArea.x -= entity.speed;
              if (entity.solidArea.intersects(gp.obj.get(i).solidArea)) {
                if (gp.obj.get(i).collision == true) {
                  entity.collisionOn = true;
                }
                if (player) {
                  collidedObject = gp.obj.get(i);
                }
              }
              break;
            case "right":
              entity.solidArea.x += entity.speed;
              if (entity.solidArea.intersects(gp.obj.get(i).solidArea)) {
                if (gp.obj.get(i).collision == true) {
                  entity.collisionOn = true;
                }
                if (player) {
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

  /**
   * Checks whether an entity's next position would overlap
   * the player or another living monster.
   *
   * @param entity Entity attempting to move
   * @param moveX  Intended horizontal movement
   * @param moveY  Intended vertical movement
   * @return true if another entity blocks movement
   */
  public boolean checkEntityCollision(
      Entity entity, double moveX, double moveY) {

    // Predict the moving entity's next hitbox
    Rectangle2D.Double nextHitbox = new Rectangle2D.Double(
        entity.WorldX + moveX + entity.solidArea.x,
        entity.WorldY + moveY + entity.solidArea.y,
        entity.solidArea.width,
        entity.solidArea.height);

    // Check collision with player (unless entity IS player)
    if (entity != gp.player) {

      Rectangle2D.Double playerHitbox = new Rectangle2D.Double(
          gp.player.WorldX + gp.player.solidArea.x,
          gp.player.WorldY + gp.player.solidArea.y,
          gp.player.solidArea.width,
          gp.player.solidArea.height);

      if (nextHitbox.intersects(playerHitbox)) {
        return true;
      }
    }

    // Check collision with every living monster
    for (Monster monster : gp.monsters) {

      // Don't collide with yourself or dead monsters
      if (monster == entity || !monster.isAlive()) {
        continue;
      }

      Rectangle2D.Double monsterHitbox = new Rectangle2D.Double(
          monster.WorldX + monster.solidArea.x,
          monster.WorldY + monster.solidArea.y,
          monster.solidArea.width,
          monster.solidArea.height);

      if (nextHitbox.intersects(monsterHitbox)) {
        return true;
      }
    }

    return false;
  }


/**
 * Checks whether an entity's next position would
 * overlap a solid object.
 *
 * Does not interact with or collect objects.
 */
public boolean checkObjectCollision(
        Entity entity, double moveX, double moveY) {

    Rectangle2D.Double nextHitbox = new Rectangle2D.Double(
        entity.WorldX + moveX + entity.solidArea.x,
        entity.WorldY + moveY + entity.solidArea.y,
        entity.solidArea.width,
        entity.solidArea.height
    );

    synchronized (gp.objLock) {

        for (SuperObject object : gp.obj) {

            if (object == null || !object.collision) {
                continue;
            }

            Rectangle2D.Double objectHitbox =
                new Rectangle2D.Double(
                    object.worldX + object.solidArea.x,
                    object.worldY + object.solidArea.y,
                    object.solidArea.width,
                    object.solidArea.height
                );

            if (nextHitbox.intersects(objectHitbox)) {
                return true;
            }
        }
    }

    return false;
}



  /**
   * Checks if an entity's intended movement would
   * collide with a solid tile or map boundary.
   */
  public boolean checkTileCollision(
      Entity entity, double moveX, double moveY) {

    int tileSize = GamePanel.tileSize;

    double left = entity.WorldX + moveX + entity.solidArea.x;
    double top = entity.WorldY + moveY + entity.solidArea.y;

    double right = left + entity.solidArea.width;
    double bottom = top + entity.solidArea.height;

    int leftCol = (int) Math.floor(left / tileSize);
    int rightCol = (int) Math.floor((right - 0.001) / tileSize);
    int topRow = (int) Math.floor(top / tileSize);
    int bottomRow = (int) Math.floor((bottom - 0.001) / tileSize);

    for (int col = leftCol; col <= rightCol; col++) {
      for (int row = topRow; row <= bottomRow; row++) {

        if (col < 0 || row < 0 ||
            col >= gp.maxWorldCol ||
            row >= gp.maxWorldRow) {
          return true;
        }

        int tileNum = gp.tileM.mapTileNum[col][row];

        if (gp.tileM.tile[tileNum].collision) {
          return true;
        }
      }
    }

    return false;
  }

}
