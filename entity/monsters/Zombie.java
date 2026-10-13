
package entity.monsters;

import java.awt.Color;
import java.awt.Graphics2D;

import main.GamePanel;
import entity.Monster;

/**
 * A basic zombie enemy.
 */
public class Zombie extends Monster {

  /**
   * Creates a zombie at a specified world position.
   */
  public Zombie(GamePanel gp, double worldX, double worldY) {
    super(gp, worldX, worldY, 50, 10);

    // Zombie movement speed
    speed = 2;
  }

  /**
   * Updates the zombie's behavior.
   */
  @Override
  public void update() {
    if (!isAlive()) {
      return;
    }

    // We'll add zombie AI here later.
  }

  /**
   * Draws the zombie relative to the player's camera.
   */
  @Override
  public void draw(Graphics2D g2) {

    if (!isAlive()) {
      return;
    }

    int screenX = (int) (WorldX - gp.player.WorldX + gp.player.screenX);

    int screenY = (int) (WorldY - gp.player.WorldY + gp.player.screenY);

    int size = GamePanel.tileSize;

    // Zombie body
    if (isDamageFlashing()) {
      g2.setColor(new Color(255, 70, 70));
    } else {
      g2.setColor(new Color(45, 110, 55));
    }
    g2.fillRect(
        screenX + size / 4,
        screenY + size / 4,
        size / 2,
        size / 2);

    // Zombie head
    if (isDamageFlashing()) {
      g2.setColor(new Color(255, 130, 130));
    } else {
      g2.setColor(new Color(90, 165, 90));
    }
    g2.fillRect(
        screenX + size / 3,
        screenY + size / 6,
        size / 3,
        size / 3);

    // Eyes
    g2.setColor(Color.RED);

    g2.fillRect(
        screenX + size / 3 + 3,
        screenY + size / 3,
        4, 4);

    g2.fillRect(
        screenX + size / 2 + 3,
        screenY + size / 3,
        4, 4);
    
    // Draw the monster's health bar
    drawHealthBar(g2);
  }

}
