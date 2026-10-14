package entity;

import main.KeyHandler;
import main.LevelHandler;
import object.SuperObject;

import com.google.gson.JsonObject;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.awt.Color;
import java.awt.image.RescaleOp;

import javax.imageio.ImageIO;

import items.*;
import combat.*;

import main.GamePanel;

public class Player extends Entity {

  GamePanel gp;
  KeyHandler keyH;

  public int currentLevel = 1;

  public final int screenX;
  public final int screenY;
  public String facingDirection = "down";

  // Player health
  private int maxHealth = 100;
  private int health = maxHealth;

  public int hasKey = 0;
  public double speedMultiplier = 1;

  // Combat
  public double attackAngle = 0;
  private double swingAngle = 0;
  public boolean attacking = false;
  private long lastAttackTime = 0;
  private AttackAnimation currentAttack;
  private WeaponPose currentWeaponPose;
  private final SwordTrail swordTrail = new SwordTrail();
  private int comboIndex = 0;
  private long lastComboTime = 0;

  // Damage flash
  private long lastDamageTime = 0;
  private static final long DAMAGE_FLASH_DURATION = 150;

  private final Set<Monster> monstersHitThisAttack = new HashSet<>();
  private boolean dummyHitThisAttack = false;

  private static final long COMBO_RESET_TIME = 1500;
  public Weapon equippedWeapon;
  private final WeaponRenderer weaponRenderer = new WeaponRenderer();

  public Player(GamePanel gp, KeyHandler keyH) {
    this.gp = gp;
    this.keyH = keyH;

    screenX = GamePanel.screenWidth / 2 - (GamePanel.tileSize / 2);
    screenY = GamePanel.screenHeight / 2 - (GamePanel.tileSize / 2);

    solidArea = new Rectangle(20, 24, 24, 32); // Player Colision Hitbox
    solidAreaDefaultX = solidArea.x;
    solidAreaDefaultY = solidArea.y;

    setValues(0);
    getPlayerImage();

    equippedWeapon = WeaponLoader.loadKatana();
  }

  public void setValues(int level) {

    if (level == 0) {
      WorldX = GamePanel.tileSize * 9;
      WorldY = GamePanel.tileSize * 3;
      speed = ((GamePanel.tileSize * gp.maxWorldCol) / 270) * speedMultiplier;
      direction = "down";
    } else {
      try {
        JsonObject playerData = LevelHandler.getPlayer(String.valueOf(level));
        WorldX = playerData.get("WorldX").getAsDouble() * GamePanel.tileSize;
        WorldY = playerData.get("WorldY").getAsDouble() * GamePanel.tileSize;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

public void getPlayerImage() {
    try {
      up1 = ImageIO.read(getClass().getResourceAsStream("/res/player/boy_up_1.png"));
      up2 = ImageIO.read(getClass().getResourceAsStream("/res/player/boy_up_2.png"));
      down1 = ImageIO.read(getClass().getResourceAsStream("/res/player/boy_down_1.png"));
      down2 = ImageIO.read(getClass().getResourceAsStream("/res/player/boy_down_2.png"));
      left1 = ImageIO.read(getClass().getResourceAsStream("/res/player/boy_left_1.png"));
      left2 = ImageIO.read(getClass().getResourceAsStream("/res/player/boy_left_2.png"));
      right1 = ImageIO.read(getClass().getResourceAsStream("/res/player/boy_right_1.png"));
      right2 = ImageIO.read(getClass().getResourceAsStream("/res/player/boy_right_2.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

public void update() {
    if (keyH.upPressed == true) {
      direction = "up";
    } else if (keyH.downPressed == true) {
      direction = "down";
    } else if (keyH.rightPressed == true) {
      direction = "right";
    } else if (keyH.leftPressed == true) {
      direction = "left";
    }

    if (keyH.upPressed == true || keyH.downPressed == true || keyH.leftPressed == true || keyH.rightPressed == true) {
      spriteCounter++;

      collisionOn = false;
      gp.cChecker.checkTile(this);

      SuperObject object = gp.cChecker.checkObject(this, true);
      interactObject(object);

      double moveX = 0;
      double moveY = 0;

      // Calculate movement based on direction
      switch (direction) {
        case "up":
          moveY = -speed;
          break;

        case "down":
          moveY = speed;
          break;

        case "left":
          moveX = -speed;
          break;

        case "right":
          moveX = speed;
          break;
      }

      // Check collision with living monsters
      if (gp.cChecker.checkEntityCollision(this, moveX, moveY)) {
        collisionOn = true;
      }

      if (!collisionOn) {
        WorldX += moveX;
        WorldY += moveY;
      }

    }
    if (spriteCounter > 15) {
      if (spriteNum == 1) {
        spriteNum = 2;
      } else if (spriteNum == 2) {
        spriteNum = 1;
      }
      spriteCounter = 0;
    }

    // Calculate the angle between the player and mouse
    int playerCenterX = screenX + gp.tileSize / 2;
    int playerCenterY = screenY + gp.tileSize / 2;

    int mouseX = gp.mouseH.mouseX;
    int mouseY = gp.mouseH.mouseY;

    attackAngle = Math.atan2(
        mouseY - playerCenterY,
        mouseX - playerCenterX);

    updateAttack();

    // Only update facing direction when necessary
    if (gp.mouseH.leftPressed) {
      // Face the mouse while holding attack
      faceMouse();
    } else if (keyH.upPressed || keyH.downPressed ||
        keyH.leftPressed || keyH.rightPressed) {
      // Face movement direction while walking
      facingDirection = direction;
    }
    // Otherwise, keep the current facing direction

    // Calculate the weapon's current position

    long currentTime = System.currentTimeMillis();

    // Calculate the weapon's position
    currentWeaponPose = calculateWeaponPose(currentTime);

    // Apply damage
    checkWeaponHit();

    // Apply Damage to Zombie
    checkMonsterHits();

    // Record the sword's movement
    updateSwordTrail(currentTime);

  }

private void checkMonsterHits() {

    if (currentWeaponPose == null ||
        !currentWeaponPose.activeStrike) {
      return;
    }

    for (Monster monster : gp.monsters) {

      // Skip dead monsters or monsters already hit
      if (!monster.isAlive() ||
          monstersHitThisAttack.contains(monster)) {
        continue;
      }

      // Get the monster's world-space hitbox
      Rectangle hitbox = new Rectangle(
          (int) monster.WorldX + monster.solidArea.x,
          (int) monster.WorldY + monster.solidArea.y,
          monster.solidArea.width,
          monster.solidArea.height);

      // Test the actual blade against the monster
      boolean hit = WeaponCollision.bladeIntersects(
          currentWeaponPose.bladeBaseX,
          currentWeaponPose.bladeBaseY,
          currentWeaponPose.tipX,
          currentWeaponPose.tipY,
          hitbox);

      if (hit) {
        monster.takeDamage(10);

        // Prevent another hit until the next attack
        monstersHitThisAttack.add(monster);
      }
    }
  }

public void interactObject(SuperObject object) {
    if (object == null) {
      return;
    }

    switch (object.name) {
      case "Key":
        hasKey++;
        synchronized (gp.objLock) {
          gp.obj.remove(object);
        }
        gp.playSE(1);
        gp.ui.showMessage("You got a key!");
        break;

      case "NextLevelDoor":
        if (hasKey > 0) {
          synchronized (gp.objLock) {
            gp.obj.remove(object);
          }
          gp.playSE(4);
          hasKey--;
          gp.setupGame(++currentLevel);
        } else {
          gp.ui.showMessage("You need a key to enter!");
        }
        break;

      case "PrevLevelDoor":
        gp.playSE(4);
        gp.setupGame(--currentLevel);
        break;

      case "Chest":
        gp.ui.gameFinished = true;
        gp.stopMusic();
        gp.playSE(2);
        break;

      case "Boots":
        speedMultiplier = 1.25;
        gp.playSE(3);
        synchronized (gp.objLock) {
          gp.obj.remove(object);
        }
        break;
    }
  }

private WeaponPose calculateWeaponPose(long currentTime) {

    if (!attacking || equippedWeapon == null ||
        currentAttack == null) {
      return null;
    }

    // Calculate animation progress
    double progress = Math.max(0.0, Math.min(
        1.0,
        (double) (currentTime - lastAttackTime)
            / currentAttack.duration));

    // Player's center in world coordinates
    double centerX = WorldX + gp.tileSize / 2.0;
    double centerY = WorldY + gp.tileSize / 2.0;

    double baseDistance = gp.tileSize * 0.3;
    double handDistance = baseDistance;
    double weaponAngle = swingAngle;

    switch (currentAttack.type) {

      case SLASH:
        double start = Math.toRadians(currentAttack.startAngle);
        double end = Math.toRadians(currentAttack.endAngle);

        double swingProgress;

        if (progress < 0.2) {
          swingProgress = 0;
        } else if (progress < 0.7) {
          double t = (progress - 0.2) / 0.5;
          swingProgress = t * t * (3.0 - 2.0 * t);
        } else {
          swingProgress = 1;
        }

        weaponAngle = swingAngle +
            start + (end - start) * swingProgress;
        break;

      case THRUST:
        weaponAngle = swingAngle;

        double extension;

        if (progress < 0.15) {
          extension = 0;
        } else if (progress < 0.45) {
          double t = (progress - 0.15) / 0.30;
          extension = 1 - Math.pow(1 - t, 3);
        } else {
          double t = Math.min(1.0,
              (progress - 0.45) / 0.55);

          extension = 1 - t * t * (3 - 2 * t);
        }

        // Keep the sword close to the player's hand
        handDistance = baseDistance +
            extension * gp.tileSize * 0.08;
        break;

      case OVERHEAD:
        double overheadProgress;

        if (progress < 0.4) {
          overheadProgress = 0;
        } else {
          overheadProgress = Math.min(
              1.0, (progress - 0.4) / 0.6);
        }

        weaponAngle = swingAngle + Math.toRadians(
            currentAttack.startAngle +
                (currentAttack.endAngle -
                    currentAttack.startAngle) * overheadProgress);
        break;
    }

    // Calculate weapon handle in world coordinates
    double handX = centerX +
        Math.cos(weaponAngle) * handDistance;

    double handY = centerY +
        Math.sin(weaponAngle) * handDistance;

    // Sprite points upward by default
    double rotation = weaponAngle + Math.PI / 2;

    int weaponSize = 48;

    // Calculate actual blade tip in world coordinates
    java.awt.geom.Point2D.Double tip = weaponRenderer.getBladeTip(
        equippedWeapon,
        handX,
        handY,
        rotation,
        weaponSize,
        weaponSize);

    // Calculate where the actual blade begins
    java.awt.geom.Point2D.Double bladeBase = weaponRenderer.getBladeBase(
        equippedWeapon,
        handX,
        handY,
        rotation,
        weaponSize,
        weaponSize);

    // Determine whether this attack can deal damage
    boolean activeStrike = false;

    switch (currentAttack.type) {
      case SLASH:
        activeStrike = progress >= 0.2 && progress <= 0.7;
        break;

      case THRUST:
        activeStrike = progress >= 0.15 && progress <= 0.55;
        break;

      case OVERHEAD:
        activeStrike = progress >= 0.4 && progress <= 0.8;
        break;
    }

    return new WeaponPose(
        handX,
        handY,
        bladeBase.x,
        bladeBase.y,
        tip.x,
        tip.y,
        rotation,
        progress,
        activeStrike);
  }

private void updateSwordTrail(long currentTime) {
    swordTrail.update(currentTime);

    if (currentWeaponPose == null ||
        currentAttack == null ||
        currentAttack.type != AttackType.SLASH ||
        !currentWeaponPose.activeStrike) {
      return;
    }

    // Save the actual blade-tip position in world space
    swordTrail.addPoint(
        currentWeaponPose.tipX,
        currentWeaponPose.tipY,
        currentTime);
  }

private void checkWeaponHit() {

    // Don't check collisions outside active attacks
    if (currentWeaponPose == null ||
        !currentWeaponPose.activeStrike ||
        dummyHitThisAttack) {
      return;
    }

    TrainingDummy dummy = gp.trainingDummy;

    // Make sure the dummy exists and is alive
    if (dummy == null || dummy.getHealth() <= 0) {
      return;
    }

    // Get the dummy's hitbox in world coordinates
    Rectangle hitbox = new Rectangle(
        (int) dummy.WorldX + dummy.solidArea.x,
        (int) dummy.WorldY + dummy.solidArea.y,
        dummy.solidArea.width,
        dummy.solidArea.height);

    // Check only the actual blade, not the handle
    boolean hit = WeaponCollision.bladeIntersects(
        currentWeaponPose.bladeBaseX,
        currentWeaponPose.bladeBaseY,
        currentWeaponPose.tipX,
        currentWeaponPose.tipY,
        hitbox);

    if (hit) {
      dummy.takeDamage(10);

      // Prevent this swing from damaging
      // the same dummy multiple times
      dummyHitThisAttack = true;
    }
  }

public void updateAttack() {
    long currentTime = System.currentTimeMillis();

    // End the current attack once its animation is finished
    if (attacking && currentAttack != null &&
        currentTime - lastAttackTime >= currentAttack.duration) {

      attacking = false;
      currentWeaponPose = null;
    }

    // Check whether the player can begin another attack
    if (!gp.mouseH.leftPressed || attacking ||
        equippedWeapon == null) {
      return;
    }

    AnimationProfile profile = equippedWeapon.getAnimationProfile();

    // Reset the combo after being idle
    if (currentTime - lastComboTime > COMBO_RESET_TIME) {
      comboIndex = 0;
    }

    // Get the next attack from the equipped weapon
    currentAttack = profile.getAttack(comboIndex);

    // Lock the mouse direction for this attack
    swingAngle = attackAngle;

    // Start each swing with a fresh trail
    swordTrail.clear();

    // Allow the next attack to damage the dummy once
    dummyHitThisAttack = false;
    monstersHitThisAttack.clear();

    // Start the attack
    attacking = true;
    lastAttackTime = currentTime;
    lastComboTime = currentTime;

    // Advance to the next attack in the combo
    comboIndex = (comboIndex + 1) % profile.getComboLength();
  }

public void faceMouse() {
    double degrees = Math.toDegrees(attackAngle);

    if (degrees >= -45 && degrees < 45) {
      facingDirection = "right";
    } else if (degrees >= 45 && degrees < 135) {
      facingDirection = "down";
    } else if (degrees >= -135 && degrees < -45) {
      facingDirection = "up";
    } else {
      facingDirection = "left";
    }
  }

public void draw(Graphics2D g2) {

    BufferedImage image = null;

    switch (facingDirection) {
      case "up":
        if (spriteNum == 1) {
          image = up1;
        }
        if (spriteNum == 2) {
          image = up2;
        }
        break;
      case "down":
        if (spriteNum == 1) {
          image = down1;
        }
        if (spriteNum == 2) {
          image = down2;
        }
        break;
      case "left":
        if (spriteNum == 1) {
          image = left1;
        }
        if (spriteNum == 2) {
          image = left2;
        }

        break;
      case "right":
        if (spriteNum == 1) {
          image = right1;
        }
        if (spriteNum == 2) {
          image = right2;
        }
        break;
    }

    if (image == null) {
      return;
    }

    // Temporarily tint the player red after taking damage.
    if (isDamageFlashing()) {
      BufferedImage tintedImage = new BufferedImage(
          image.getWidth(),
          image.getHeight(),
          BufferedImage.TYPE_INT_ARGB);

      // Preserve the original image and transparency.
      Graphics2D tintGraphics = tintedImage.createGraphics();
      tintGraphics.drawImage(image, 0, 0, null);
      tintGraphics.dispose();

      // Increase red while reducing green and blue.
      float[] scales = { 1.0f, 0.35f, 0.35f, 1.0f };
      float[] offsets = { 80f, 0f, 0f, 0f };

      RescaleOp redTint = new RescaleOp(scales, offsets, null);
      redTint.filter(tintedImage, tintedImage);

      g2.drawImage(tintedImage, screenX, screenY,
          GamePanel.tileSize, GamePanel.tileSize, null);
    } else {
      g2.drawImage(image, screenX, screenY,
          GamePanel.tileSize, GamePanel.tileSize, null);
    }

  }

public void drawWeaponPose(Graphics2D g2) {

    // Convert weapon world coordinates to screen coordinates
    double cameraX = WorldX - screenX;
    double cameraY = WorldY - screenY;

    // Draw the sword's recent movement trail
    swordTrail.draw(
        g2,
        cameraX,
        cameraY,
        System.currentTimeMillis());

    if (currentWeaponPose == null ||
        equippedWeapon == null) {
      return;
    }

    double handScreenX = currentWeaponPose.handleX - cameraX;
    double handScreenY = currentWeaponPose.handleY - cameraY;

    // Draw the weapon using its calculated pose
    weaponRenderer.draw(
        g2,
        equippedWeapon,
        handScreenX,
        handScreenY,
        currentWeaponPose.rotation,
        48,
        48);
  }

public void takeDamage(int damage) {
    if (damage <= 0 || !isAlive()) {
        return;
    }

    int actualDamage = Math.min(damage, health);

    health -= actualDamage;

    // Trigger damage flash.
    lastDamageTime = System.currentTimeMillis();

    // Display a floating damage number.
    gp.damageNumbers.add(
            actualDamage,
            WorldX + GamePanel.tileSize / 2.0,
            WorldY
    );

    if (!isAlive()) {
        onDeath();
    }
}

private void onDeath() {
    System.out.println("Player defeated!");
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

public void heal(int amount) {
    if (amount <= 0 || !isAlive()) {
        return;
    }

    health = Math.min(maxHealth, health + amount);
}

private boolean isDamageFlashing() {
    return lastDamageTime != 0 &&
            System.currentTimeMillis() - lastDamageTime
                    < DAMAGE_FLASH_DURATION;
}
}