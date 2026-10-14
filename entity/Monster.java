
package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import main.GamePanel;
import entity.definitions.MonsterDefinition;
import entity.ai.MonsterNavigator;

/**
 * Base class for monsters in DungeonNinja.
 *
 * Contains shared properties that all enemy types
 * will use, such as health, damage, and position.
 */
public abstract class Monster extends Entity {

  protected final GamePanel gp;
  protected final MonsterDefinition definition;
  protected final MonsterNavigator navigator = new MonsterNavigator();

  protected int maxHealth;
  protected int health;
  protected int attackDamage;

  // Damage flash
  protected long lastDamageTime = 0;
  protected static final long DAMAGE_FLASH_DURATION = 150;

  /**
   * Creates a monster using a reusable definition.
   *
   * @param gp         Game panel
   * @param definition Monster type and attributes
   * @param worldX     Starting world X coordinate
   * @param worldY     Starting world Y coordinate
   */
  public Monster(
      GamePanel gp,
      MonsterDefinition definition,
      double worldX,
      double worldY) {

    this.gp = gp;
    this.definition = definition;

    this.WorldX = worldX;
    this.WorldY = worldY;

    // Initialize attributes from definition
    this.maxHealth = definition.getMaxHealth();
    this.health = maxHealth;
    this.attackDamage = definition.getAttackDamage();
    this.speed = definition.getSpeed();

    // Set up collision hitbox
    this.solidArea = new Rectangle(
        8,
        8,
        GamePanel.tileSize - 16,
        GamePanel.tileSize - 16);

    this.direction = "down";
  }

  /**
   * Calculates the distance between this monster
   * and the player, measured in pixels.
   *
   * @return Distance to the player
   */
  protected double distanceToPlayer() {

    double monsterCenterX = WorldX + GamePanel.tileSize / 2.0;
    double monsterCenterY = WorldY + GamePanel.tileSize / 2.0;

    double playerCenterX = gp.player.WorldX + GamePanel.tileSize / 2.0;
    double playerCenterY = gp.player.WorldY + GamePanel.tileSize / 2.0;

    return Math.hypot(
        playerCenterX - monsterCenterX,
        playerCenterY - monsterCenterY);
  }

  /**
   * Checks whether the player is within this monster's
   * configured detection range.
   *
   * @return true if the player is close enough to detect
   */
  protected boolean detectPlayer() {

    return distanceToPlayer() <= definition.getDetectionRange() * GamePanel.tileSize;
  }

  public MonsterDefinition getDefinition() {
    return definition;
  }

  /**
   * Checks whether the monster's next movement
   * would collide with the player.
   *
   * @param moveX Intended horizontal movement
   * @param moveY Intended vertical movement
   * @return true if the movement would hit the player
   */
  protected boolean checkPlayerCollision(double moveX, double moveY) {

    Rectangle playerHitbox = new Rectangle(
        (int) gp.player.WorldX + gp.player.solidArea.x,
        (int) gp.player.WorldY + gp.player.solidArea.y,
        gp.player.solidArea.width,
        gp.player.solidArea.height);

    // Predict the monster's next position
    Rectangle nextHitbox = new Rectangle(
        (int) Math.floor(WorldX + moveX + solidArea.x),
        (int) Math.floor(WorldY + moveY + solidArea.y),
        solidArea.width,
        solidArea.height);

    return nextHitbox.intersects(playerHitbox);
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

  // Draws a health bar above the monster.
  protected void drawHealthBar(Graphics2D g2) {

    if (!isAlive()) {
      return;
    }

    // Convert world position to screen position
    int screenX = (int) (WorldX - gp.player.WorldX + gp.player.screenX);

    int screenY = (int) (WorldY - gp.player.WorldY + gp.player.screenY);

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
   * Attempts to move the monster while checking collisions.
   *
   * Currently supports four-direction movement.
   */

  protected boolean tryMove(double moveX, double moveY) {

    if (!isAlive() || (moveX == 0 && moveY == 0)) {
      return false;
    }

    // Tile collision using the actual movement vector
    boolean tileBlocked = gp.cChecker.checkTileCollision(this, moveX, moveY);

    // Existing object collision still uses direction and speed
    boolean objectBlocked =
    gp.cChecker.checkObjectCollision(this, moveX, moveY);

    // Player and other monster collision
    boolean entityBlocked = gp.cChecker.checkEntityCollision(this, moveX, moveY);

    if (tileBlocked || objectBlocked || entityBlocked) {
      return false;
    }

    WorldX += moveX;
    WorldY += moveY;

    return true;
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

  public MonsterNavigator getNavigator() {
    return navigator;
  }

  public GamePanel getGamePanel() {
    return gp;
  }

  /**
   * Updates the monster's behavior.
   */
  public abstract void update();

  // Draws the monster in the game world.
  public abstract void draw(Graphics2D g2);

}
