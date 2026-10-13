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



import main.GamePanel;

public class Player extends Entity{
  
  GamePanel gp;
  KeyHandler keyH;

public int currentLevel = 1;

  public final int screenX;
  public final int screenY;
  public int hasKey = 0;
  public double speedMultiplier = 1;

  public Player(GamePanel gp, KeyHandler keyH) {
    this.gp = gp;
    this.keyH = keyH;

    screenX = GamePanel.screenWidth/2 - (GamePanel.tileSize/2);
    screenY = GamePanel.screenHeight/2 - (GamePanel.tileSize/2);

    solidArea = new Rectangle(20,24,24,32);    //Player Colision Hitbox
    solidAreaDefaultX = solidArea.x;
    solidAreaDefaultY = solidArea.y;

    setValues(0);
    getPlayerImage();
  }

  public void setValues(int level) {
    
    if (level == 0) {
      WorldX = GamePanel.tileSize *9;
      WorldY = GamePanel.tileSize *3;
      speed = ((GamePanel.tileSize * gp.maxWorldCol)/270)* speedMultiplier;
      direction = "down";
    } else {
      try {
      JsonObject playerData = LevelHandler.getPlayer(String.valueOf(level));
      WorldX = playerData.get("WorldX").getAsDouble() * GamePanel.tileSize;
      WorldY = playerData.get("WorldY").getAsDouble() * GamePanel.tileSize; 
    } catch (Exception e) {e.printStackTrace();}
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
    }catch(IOException e) {e.printStackTrace();}
  }

  public void update() {
    if(keyH.upPressed == true) {
        direction = "up";
      } else if(keyH.downPressed == true) {
        direction = "down";
      } else if(keyH.rightPressed == true) {
        direction = "right";
      } else if(keyH.leftPressed == true) {
        direction = "left";
      } 

      if(keyH.upPressed == true || keyH.downPressed == true || keyH.leftPressed == true|| keyH.rightPressed == true) {
        spriteCounter++;

        collisionOn = false;
      gp.cChecker.checkTile(this);

      SuperObject object = gp.cChecker.checkObject(this, true);
      interactObject(object);


      if(collisionOn == false) {
        switch(direction) {
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
      if(spriteCounter > 15) {
        if(spriteNum == 1) {spriteNum =2;}
        else if(spriteNum ==2) {spriteNum =1;}
        spriteCounter = 0;
      }

    
  }

  public void interactObject(SuperObject object) {
    if (object == null) {
      return;
    }
          
    switch (object.name) {
      case "Key":
        hasKey++;
        synchronized (gp.objLock) {gp.obj.remove(object);}
        gp.playSE(1);
        gp.ui.showMessage("You got a key!");
        break;

      case "NextLevelDoor":
        if (hasKey > 0) {
          synchronized (gp.objLock) {gp.obj.remove(object);}
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
        synchronized (gp.objLock) {gp.obj.remove(object);}
        break;
    }
  }
   
  public void draw(Graphics2D g2) {

   BufferedImage image = null;

   switch(direction) {
    case "up":
      if(spriteNum == 1) {image = up1;}
      if(spriteNum == 2) {image = up2;}
      break;
    case "down":
      if(spriteNum == 1) {image = down1;}
      if(spriteNum == 2) {image = down2;}
      break;
    case "left":
      if(spriteNum == 1) {image = left1;}
      if(spriteNum == 2) {image = left2;}

      break;
    case "right":
      if(spriteNum == 1) {image = right1;}
      if(spriteNum == 2) {image = right2;}
      break;
   }
   g2.drawImage(image, screenX, screenY, GamePanel.tileSize, GamePanel.tileSize, null);
  }

}
