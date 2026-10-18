
package entity.monsters;

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

    loadSprite(definition.getSpritePath());
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

    // Temporarily interrupt chasing while being knocked back.
    if (updateKnockback()) {
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
        spriteCounter++;
        break;

      case ATTACKING:
        attackPlayer();
        break;

      case RECOVERING:
        // Recovery is currently handled by MonsterNavigator.
        chasePlayer();
        break;
    }

    if (spriteCounter > 35) {
      if (spriteNum == 1) {
        spriteNum = 2;
      } else if (spriteNum == 2) {
        spriteNum = 1;
      }
      spriteCounter = 0;
    }
  }

  /**
   * Draws the zombie relative to the player's camera.
   */
  @Override
  public void draw(Graphics2D g2) {
    drawMonsterSprite(g2);
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
