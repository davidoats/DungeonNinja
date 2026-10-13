package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import object.OBJ_Key;

public class UI {

  GamePanel gp;
  Font arialFont = new Font("Arial", Font.PLAIN, 40);
  BufferedImage keyImage = new OBJ_Key().image;

  public boolean gameFinished = false;

  public Boolean messageOn = false;
  public String message = "";
  int messageCounter = 0;

  public UI(GamePanel gp) {
    this.gp = gp;
  }

  public void showMessage(String text) {
    message = text;
    messageOn = true;
  }

  public void draw(Graphics2D g2) {

    if (gameFinished) {
      g2.setFont(arialFont);
      g2.setColor(Color.WHITE);
      String text = "You Found the Treasure!";

      int x = GamePanel.screenWidth / 2 - ((int) g2.getFontMetrics().getStringBounds(text, g2).getWidth()) / 2;
      int y = GamePanel.screenHeight / 2;

      g2.drawString(text, x, y);

    } else {
      g2.setFont(arialFont);
      g2.setColor(Color.WHITE);
      g2.drawImage(keyImage, GamePanel.tileSize / 2, GamePanel.tileSize / 2, GamePanel.tileSize, GamePanel.tileSize,
          null);
      g2.drawString("x " + gp.player.hasKey, 95, 80);

      if (messageOn) {
        g2.setFont(g2.getFont().deriveFont(30F));
        g2.drawString(message, 420, 300);
        messageCounter++;
        if (messageCounter > 2 * gp.FPS) {
          messageCounter = 0;
          messageOn = false;
        }
      }
    }

  }

}