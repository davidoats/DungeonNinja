
package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import main.GamePanel;
import entity.definitions.MonsterDefinition;
import entity.ai.MonsterNavigator;
import entity.ai.MonsterState;

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
  protected MonsterState state = MonsterState.IDLE;

  protected int maxHealth;
  protected int health;
  protected int attackDamage;

  protected long lastAttackTime = 0;
  protected long attackCooldown = 1000;

  // Damage flash
  protected long lastDamageTime = 0;
  protected static final long DAMAGE_FLASH_DURATION = 150;

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

  protected double distanceToPlayer() {

    double monsterCenterX = WorldX + GamePanel.tileSize / 2.0;
    double monsterCenterY = WorldY + GamePanel.tileSize / 2.0;

    double playerCenterX = gp.player.WorldX + GamePanel.tileSize / 2.0;
    double playerCenterY = gp.player.WorldY + GamePanel.tileSize / 2.0;

    return Math.hypot(
        playerCenterX - monsterCenterX,
        playerCenterY - monsterCenterY);
  }

  protected boolean detectPlayer() {

    return distanceToPlayer() <= definition.getDetectionRange() * GamePanel.tileSize;
  }

  public MonsterDefinition getDefinition() {
    return definition;
  }

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

  protected boolean isDamageFlashing() {
    return lastDamageTime != 0 &&
        System.currentTimeMillis() - lastDamageTime < DAMAGE_FLASH_DURATION;
  }

  protected boolean tryMove(double moveX, double moveY) {

    if (!isAlive() || (moveX == 0 && moveY == 0)) {
      return false;
    }

    // Tile collision using the actual movement vector
    boolean tileBlocked = gp.cChecker.checkTileCollision(this, moveX, moveY);

    // Existing object collision still uses direction and speed
    boolean objectBlocked = gp.cChecker.checkObjectCollision(this, moveX, moveY);

    // Player and other monster collision
    boolean entityBlocked = gp.cChecker.checkEntityCollision(this, moveX, moveY);

    if (tileBlocked || objectBlocked || entityBlocked) {
      return false;
    }

    WorldX += moveX;
    WorldY += moveY;

    return true;
  }

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

  public abstract void update();

  public abstract void draw(Graphics2D g2);

  public MonsterState getState() {
    return state;
  }

  protected void setState(MonsterState newState) {
    if (newState == null || state == newState) {
      return;
    }

    state = newState;
  }

  protected void updateBehaviorState() {

    if (!isAlive()) {
      return;
    }

    // Do not interrupt navigation recovery.
    if (state == MonsterState.RECOVERING) {
      return;
    }

    // Player is outside detection range.
    if (!detectPlayer()) {
      setState(MonsterState.IDLE);
      return;
    }

    // Check whether the player is within attack range.
    double attackDistance = definition.getAttackRange() * GamePanel.tileSize;

    if (distanceToPlayer() <= attackDistance) {
      setState(MonsterState.ATTACKING);
    } else {
      setState(MonsterState.CHASING);
    }
  }

  protected boolean shouldChasePlayer() {
    return state == MonsterState.CHASING;
  }

  protected boolean canAttack() {
    return isAlive()
        && System.currentTimeMillis() - lastAttackTime >= attackCooldown;
  }

  protected void startAttackCooldown() {
    lastAttackTime = System.currentTimeMillis();
  }

  public void setAttackCooldown(long cooldown) {
    if (cooldown < 0) {
      throw new IllegalArgumentException(
          "Attack cooldown cannot be negative.");
    }

    attackCooldown = cooldown;
  }

  public long getAttackCooldown() {
    return attackCooldown;
  }

protected boolean attackPlayer() {

    if (!isAlive() || !gp.player.isAlive()) {
        return false;
    }

    // Verify the player is still within attack range.
    double attackRange =
            definition.getAttackRange() * GamePanel.tileSize;

    if (distanceToPlayer() > attackRange) {
        return false;
    }

    // Prevent attacks from occurring every frame.
    if (!canAttack()) {
        return false;
    }

    gp.player.takeDamage(attackDamage);

    startAttackCooldown();

    return true;
}

}
