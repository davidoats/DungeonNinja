
package entity.monsters;

import java.awt.Color;
import java.awt.Graphics2D;

import main.GamePanel;
import entity.Monster;
import entity.definitions.MonsterDefinitions;

/**
 * A basic zombie enemy.
 */
public class Zombie extends Monster {

  /**
   * Creates a zombie at a specified world position.
   */
  public Zombie(GamePanel gp, double worldX, double worldY) {
    super(gp, MonsterDefinitions.ZOMBIE, worldX, worldY);
  }

  /**
   * Updates the zombie's behavior.
   */

  /**
   * Updates the zombie's behavior based on its AI state.
   */
  @Override
  public void update() {
    if (!isAlive()) {
      return;
    }

    // Determine whether to idle, chase, or attack.
    updateBehaviorState();

    switch (state) {
      case IDLE:
        // No player detected.
        break;

      case CHASING:
        chasePlayer();
        break;

      case ATTACKING:
        attackPlayer();
        break;

      case RECOVERING:
        // Recovery is currently handled by MonsterNavigator.
        chasePlayer();
        break;
    }
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

  // Moves the zombie toward the player.

  private void chasePlayer() {

    // Stop when within configured attack range
    if (distanceToPlayer() <= definition.getAttackRange() * GamePanel.tileSize) {
      return;
    }

    // Ask the navigator where to move
    double[] movement = navigator.getMovement(
        this,
        gp.player.WorldX,
        gp.player.WorldY);

    double moveX = movement[0];
    double moveY = movement[1];

    // Face the direction we're moving
    if (moveX > 0) {
      direction = "right";
    } else if (moveX < 0) {
      direction = "left";
    } else if (moveY > 0) {
      direction = "down";
    } else if (moveY < 0) {
      direction = "up";
    }

    // Monster handles collision and movement
    boolean moved = tryMove(moveX, moveY);

    if (moveX != 0 || moveY != 0) {
      navigator.reportMovementResult(moved);
    }
  }

}
