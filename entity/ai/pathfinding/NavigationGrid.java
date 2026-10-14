
package entity.ai.pathfinding;

import entity.Monster;
import java.awt.geom.Rectangle2D;

import main.GamePanel;

/**
 * Connects pathfinding to the game's tile map.
 *
 * This class determines whether a map tile is walkable.
 */
public class NavigationGrid implements AStarPathfinder.Walkable {

  private final GamePanel gp;
  private final Monster monster;

  public NavigationGrid(GamePanel gp, Monster monster) {
    this.gp = gp;
    this.monster = monster;
  }

  /**
   * Checks whether the given tile can be entered.
   *
   * @param col Tile column
   * @param row Tile row
   * @return true if the tile is walkable
   */

  @Override
  public boolean isWalkable(int col, int row) {

    int tileSize = GamePanel.tileSize;

    // Check map boundaries
    if (col < 0 || row < 0 ||
        col >= gp.maxWorldCol ||
        row >= gp.maxWorldRow) {
      return false;
    }

    // Predict the monster's collision box at this tile
    double worldX = col * tileSize;
    double worldY = row * tileSize;

    Rectangle2D.Double hitbox = new Rectangle2D.Double(
        worldX + monster.solidArea.x,
        worldY + monster.solidArea.y,
        monster.solidArea.width,
        monster.solidArea.height);

    // Find all tiles touched by the hitbox
    int leftCol = (int) Math.floor(hitbox.x / tileSize);
    int rightCol = (int) Math.floor(
        (hitbox.x + hitbox.width - 0.001) / tileSize);

    int topRow = (int) Math.floor(hitbox.y / tileSize);
    int bottomRow = (int) Math.floor(
        (hitbox.y + hitbox.height - 0.001) / tileSize);

    for (int x = leftCol; x <= rightCol; x++) {
      for (int y = topRow; y <= bottomRow; y++) {

        if (x < 0 || y < 0 ||
            x >= gp.maxWorldCol ||
            y >= gp.maxWorldRow) {
          return false;
        }

        int tileNum = gp.tileM.mapTileNum[x][y];

        if (gp.tileM.tile[tileNum].collision) {
          return false;
        }
      }
    }

    return true;
  }


/**
 * Checks whether a monster can travel between
 * two neighboring tile positions.
 *
 * Checks the monster's full collision box at
 * intermediate positions along the movement.
 */
public boolean canTravel(
        int fromCol, int fromRow,
        int toCol, int toRow) {

    // Only allow movement to adjacent tiles
    int distance = Math.abs(toCol - fromCol)
                 + Math.abs(toRow - fromRow);

    if (distance != 1) {
        return false;
    }

    int tileSize = GamePanel.tileSize;

    double startX = fromCol * tileSize;
    double startY = fromRow * tileSize;

    double endX = toCol * tileSize;
    double endY = toRow * tileSize;

    // Check several positions between the two tiles
    for (int step = 1; step <= tileSize; step++) {

        double progress = (double) step / tileSize;

        double testX = startX + (endX - startX) * progress;
        double testY = startY + (endY - startY) * progress;

        double moveX = testX - monster.WorldX;
        double moveY = testY - monster.WorldY;

        if (gp.cChecker.checkTileCollision(
                monster, moveX, moveY)) {
            return false;
        }

        if (gp.cChecker.checkObjectCollision(
                monster, moveX, moveY)) {
            return false;
        }
    }

    return true;
}


}
