package object;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import main.GamePanel;

public class SuperObject {

  public BufferedImage image;
  public String name;
  public boolean collision = false;
  public double worldX, worldY;
  public Rectangle solidArea = new Rectangle(0, 0, 64, 64);
  public int solidAreaDefaultX = 0;
  public int solidAreaDefaultY = 0;

  public void draw(Graphics2D g2, GamePanel gp) {

    double screenX = worldX - gp.player.WorldX + gp.player.screenX;
    double screenY = worldY - gp.player.WorldY + gp.player.screenY;

    // basicly Render Distance
    if (worldX + 1 * (GamePanel.tileSize) > gp.player.WorldX - gp.player.screenX &&
        worldX - 2 * GamePanel.tileSize < gp.player.WorldX + gp.player.screenX &&
        worldY + 1 * GamePanel.tileSize > gp.player.WorldY - gp.player.screenY &&
        worldY - 2 * GamePanel.tileSize < gp.player.WorldY + gp.player.screenY) {
      g2.drawImage(image, (int) screenX, (int) screenY, GamePanel.tileSize, GamePanel.tileSize, null);
    }
  }
}