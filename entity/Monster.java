
package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import main.GamePanel;

/**
 * Base class for monsters in DungeonNinja.
 *
 * Contains shared properties that all enemy types
 * will use, such as health, damage, and position.
 */
public abstract class Monster extends Entity {

  protected final GamePanel gp;

  protected int maxHealth;
  protected int health;
  protected int attackDamage;

  // Damage flash
  protected long lastDamageTime = 0;
  protected static final long DAMAGE_FLASH_DURATION = 150;

  /**
   * Creates a monster at a specified world position.
   *
   * @param gp           Game panel
   * @param worldX       Starting world X coordinate
   * @param worldY       Starting world Y coordinate
   * @param maxHealth    Monster's maximum health
   * @param attackDamage Damage dealt by the monster
   */
  public Monster(GamePanel gp, double worldX, double worldY,
      int maxHealth, int attackDamage) {

    this.gp = gp;

    WorldX = worldX;
    WorldY = worldY;

    this.maxHealth = maxHealth;
    this.health = maxHealth;
    this.attackDamage = attackDamage;

    // Default monster collision area
    solidArea = new Rectangle(
        8, 8,
        GamePanel.tileSize - 16,
        GamePanel.tileSize - 16);

    solidAreaDefaultX = solidArea.x;
    solidAreaDefaultY = solidArea.y;

    direction = "down";
  }

  /**
   * Reduces the monster's health.
   */
  public void takeDamage(int damage) {

    if (damage <= 0 || health <= 0) {
      return;
    }

    int actualDamage = Math.min(damage, health);

    health -= actualDamage;

    // Start the damage flash
    lastDamageTime = System.currentTimeMillis();

    // Display floating damage number
    gp.damageNumbers.add(
        actualDamage,
        WorldX + GamePanel.tileSize / 2.0,
        WorldY);

    if (health == 0) {
      onDeath();
    }
  }

  //Draws a health bar above the monster.
protected void drawHealthBar(Graphics2D g2) {

    if (!isAlive()) {
        return;
    }

    // Convert world position to screen position
    int screenX = (int) (
        WorldX - gp.player.WorldX + gp.player.screenX
    );

    int screenY = (int) (
        WorldY - gp.player.WorldY + gp.player.screenY
    );

    // Health bar dimensions
    int barWidth = GamePanel.tileSize;
    int barHeight = 6;

    // Position above monster
    int barX = screenX;
    int barY = screenY - 12;

    // Calculate remaining health
    double healthPercent = (double) health / maxHealth;

    int healthWidth = (int) (barWidth * healthPercent);

    // Background
    g2.setColor(Color.DARK_GRAY);
    g2.fillRect(barX, barY, barWidth, barHeight);

    // Remaining health
    g2.setColor(Color.RED);
    g2.fillRect(barX, barY, healthWidth, barHeight);

    // Border
    g2.setColor(Color.BLACK);
    g2.drawRect(barX, barY, barWidth, barHeight);
}

  // Returns true while the monster is flashing from damage.
  protected boolean isDamageFlashing() {
    return lastDamageTime != 0 &&
        System.currentTimeMillis() - lastDamageTime < DAMAGE_FLASH_DURATION;
  }

  /**
   * Called when the monster dies.
   * Specific enemy types can override this.
   */
  protected void onDeath() {
    System.out.println(getClass().getSimpleName() + " defeated!");
  }

  public boolean isAlive() {
    return health > 0;
  }

  public int getHealth() {
    return health;
  }

  public int getMaxHealth() {
    return maxHealth;
  }

  public int getAttackDamage() {
    return attackDamage;
  }

  /**
   * Updates the monster's behavior.
   */
  public abstract void update();

  // Draws the monster in the game world.
  public abstract void draw(Graphics2D g2);

}
