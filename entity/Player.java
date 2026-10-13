package entity;

import main.KeyHandler;
import main.LevelHandler;
import object.SuperObject;

import com.google.gson.JsonObject;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import items.Weapon;
import items.WeaponLoader;
import combat.WeaponRenderer;
import combat.WeaponCollision;
import combat.AttackAnimation;
import combat.AnimationProfile;
import combat.AttackType;
import combat.SwordTrail;
import java.awt.geom.Point2D;

import main.GamePanel;

public class Player extends Entity {

  GamePanel gp;
  KeyHandler keyH;

  public int currentLevel = 1;

  public final int screenX;
  public final int screenY;
  public String facingDirection = "down";

  public int hasKey = 0;
  public double speedMultiplier = 1;

  // Combat
  public double attackAngle = 0;
  private double swingAngle = 0;
  public boolean attacking = false;
  private long lastAttackTime = 0;
  private AttackAnimation currentAttack;
  private final SwordTrail swordTrail = new SwordTrail();
  private int comboIndex = 0;
  private long lastComboTime = 0;

  private boolean dummyHitThisAttack = false;

  private Rectangle getDummyHitbox() {

    if (gp.trainingDummy == null) {
      return null;
    }

    TrainingDummy dummy = gp.trainingDummy;

    return new Rectangle(
        (int) dummy.WorldX + dummy.solidArea.x,
        (int) dummy.WorldY + dummy.solidArea.y,
        dummy.solidArea.width,
        dummy.solidArea.height);
  }

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

      if (collisionOn == false) {
        switch (direction) {
          case "up":
            WorldY = WorldY - speed;
            break;
          case "down":
            WorldY = WorldY + speed;
            break;
          case "left":
            WorldX = WorldX - speed;
            break;
          case "right":
            WorldX = WorldX + speed;
            break;
        }
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

  public void updateAttack() {
    long currentTime = System.currentTimeMillis();

    // End the current attack once its animation is finished
    if (attacking && currentAttack != null &&
        currentTime - lastAttackTime >= currentAttack.duration) {

      attacking = false;
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

    // Start the attack
    attacking = true;
    lastAttackTime = currentTime;
    lastComboTime = currentTime;

    // Advance to the next attack in the combo
    comboIndex = (comboIndex + 1) % profile.getComboLength();

    System.out.println(
        equippedWeapon.getName() +
            " used " + currentAttack.type +
            " (" + currentAttack.duration + " ms)");
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
    g2.drawImage(image, screenX, screenY, GamePanel.tileSize, GamePanel.tileSize, null);
  }

  public void drawSword(Graphics2D g2) {

    if (!attacking || equippedWeapon == null || currentAttack == null) {
      return;
    }

    // Player center on screen
    double centerX = screenX + gp.tileSize / 2.0;
    double centerY = screenY + gp.tileSize / 2.0;

    // Animation progress (0.0 to 1.0)
    long elapsed = System.currentTimeMillis() - lastAttackTime;

    double progress = Math.max(0.0, Math.min(
        1.0,
        (double) elapsed / currentAttack.duration));

    // Base distance from player to weapon handle
    double baseDistance = gp.tileSize * 0.3;

    // Default weapon position
    double weaponAngle = swingAngle;
    double handDistance = baseDistance;

    switch (currentAttack.type) {

      case SLASH:
        // Read swing angles from the animation profile
        double start = Math.toRadians(currentAttack.startAngle);
        double end = Math.toRadians(currentAttack.endAngle);

        double swingProgress;

        if (progress < 0.2) {
          // Windup: hold the weapon near its starting position
          swingProgress = 0;
        } else if (progress < 0.7) {
          // Strike: sweep quickly through the attack
          double t = (progress - 0.2) / 0.5;
          swingProgress = t * t * (3.0 - 2.0 * t);
        } else {
          // Recovery: hold the finishing position
          swingProgress = 1;
        }

        weaponAngle = swingAngle + start + (end - start) * swingProgress;
        break;

      case THRUST:
        // Keep the sword aimed forward
        weaponAngle = swingAngle;

        double extension;

        if (progress < 0.15) {
          // Windup: hold sword close to player
          extension = 0;
        } else if (progress < 0.45) {
          // Fast forward thrust
          double t = (progress - 0.15) / 0.30;
          extension = 1 - Math.pow(1 - t, 3);
        } else {
          // Slower withdrawal
          double t = Math.min(1.0,
              (progress - 0.45) / 0.55);
          extension = 1 - t * t * (3 - 2 * t);
        }

        handDistance = baseDistance + extension * gp.tileSize * 0.08;
        break;

      case OVERHEAD:
        // Slower windup followed by a quick strike
        double overheadProgress;

        if (progress < 0.4) {
          overheadProgress = 0;
        } else {
          overheadProgress = (progress - 0.4) / 0.6;
        }

        weaponAngle = swingAngle +
            Math.toRadians(
                currentAttack.startAngle +
                    (currentAttack.endAngle -
                        currentAttack.startAngle) * overheadProgress);

        break;
    }

    // Position weapon relative to player
    double handX = centerX +
        Math.cos(weaponAngle) * handDistance;

    double handY = centerY +
        Math.sin(weaponAngle) * handDistance;

    // Sprite points upward by default
    double weaponRotation = weaponAngle + Math.PI / 2;

    int weaponSize = 48;

    // Calculate sword tip in screen coordinates
    Point2D.Double tip = weaponRenderer.getBladeTip(
        equippedWeapon,
        handX,
        handY,
        weaponRotation,
        weaponSize,
        weaponSize);

    // Calculate the camera offset
    double cameraX = WorldX - screenX;
    double cameraY = WorldY - screenY;

    // Convert sword tip from screen to world coordinates
    double tipWorldX = tip.x + cameraX;
    double tipWorldY = tip.y + cameraY;

    // Check whether the active sword attack hits the dummy
    if (!dummyHitThisAttack &&
        gp.trainingDummy != null &&
        gp.trainingDummy.getHealth() > 0) {

      boolean activeStrike = false;

      // Only deal damage during the actual attack motion
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

      if (activeStrike) {

        // Convert weapon handle position to world coordinates
        double handleWorldX = handX + cameraX;
        double handleWorldY = handY + cameraY;

        Rectangle dummyHitbox = getDummyHitbox();

        if (WeaponCollision.bladeIntersects(
            handleWorldX,
            handleWorldY,
            tipWorldX,
            tipWorldY,
            dummyHitbox)) {

          gp.trainingDummy.takeDamage(10);

          // Prevent multiple hits during the same attack
          dummyHitThisAttack = true;
        }
      }
    }

    long currentTime = System.currentTimeMillis();

    // Record the blade tip during the active slash
    if (currentAttack.type == AttackType.SLASH &&
        progress >= 0.2 && progress <= 0.7) {

      swordTrail.addPoint(
          tipWorldX,
          tipWorldY,
          currentTime);
    }

    // Draw the trail using the current camera position
    swordTrail.draw(
        g2,
        cameraX,
        cameraY,
        currentTime);

    // Draw weapon
    weaponRenderer.draw(
        g2,
        equippedWeapon,
        handX,
        handY,
        weaponRotation,
        weaponSize,
        weaponSize);
  }

}
