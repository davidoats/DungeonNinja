
package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Composite;
import java.awt.image.BufferedImage;

import object.OBJ_Key;

public class UI {

  private final GamePanel gp;

  private final Font titleFont = new Font("Monospaced", Font.BOLD, 16);
  private final Font smallFont = new Font("Monospaced", Font.BOLD, 18);
  private final Font messageFont = new Font("Monospaced", Font.BOLD, 28);

  private final BufferedImage keyImage = new OBJ_Key().image;

  public boolean gameFinished = false;

  public Boolean messageOn = false;
  public String message = "";
  private int messageCounter = 0;

  private static final int HUD_X = 24;
  private static final int HUD_Y = 24;
  private static final int BAR_WIDTH = 190;
  private static final int BAR_HEIGHT = 22;

  private static final Color BORDER = new Color(28, 24, 32);
  private static final Color RED = new Color(215, 55, 65);

  public UI(GamePanel gp) {
    this.gp = gp;
  }

  public void showMessage(String text) {
    message = text;
    messageOn = true;
  }

  public void draw(Graphics2D g2) {
    if (gameFinished) {
      drawVictory(g2);
      return;
    }

    drawHealth(g2);
    drawKeys(g2);
    drawMessage(g2);
  }

  /**
   * Draws the player's health bar and heart.
   */
  private void drawHealth(Graphics2D g2) {

    int x = HUD_X;
    int y = HUD_Y;

    int health = gp.player.getHealth();
    int maxHealth = gp.player.getMaxHealth();

    double percentage = maxHealth > 0
        ? Math.max(0.0, Math.min(1.0,
            (double) health / maxHealth))
        : 0;

    // Heart icon
    drawHeart(g2, x, y + 9, 4);

    int barX = x + 52;
    int barY = y + 20;

    g2.setFont(titleFont);
    g2.setColor(Color.WHITE);
    g2.drawString("HEALTH", barX, y + 12);

    // Dark outer frame
    g2.setColor(BORDER);
    g2.fillRect(
        barX - 4,
        barY - 4,
        BAR_WIDTH + 8,
        BAR_HEIGHT + 8);

    // Light pixel border
    g2.setColor(new Color(165, 160, 156));
    g2.fillRect(
        barX - 2,
        barY - 2,
        BAR_WIDTH + 4,
        BAR_HEIGHT + 4);

    // Empty health bar
    g2.setColor(new Color(65, 32, 41));
    g2.fillRect(barX, barY, BAR_WIDTH, BAR_HEIGHT);

    // Current health
    int filled = (int) Math.round(
        BAR_WIDTH * percentage);

    g2.setColor(RED);
    g2.fillRect(barX, barY, filled, BAR_HEIGHT);

    // Pixel highlight
    g2.setColor(new Color(245, 112, 112));
    g2.fillRect(
        barX,
        barY,
        filled,
        Math.min(5, BAR_HEIGHT));

    // Health text
    g2.setFont(smallFont);
    g2.setColor(Color.WHITE);

    String text = health + "/" + maxHealth;

    int textX = barX + (BAR_WIDTH -
        g2.getFontMetrics().stringWidth(text)) / 2;

    g2.drawString(
        text,
        textX,
        barY + BAR_HEIGHT - 4);

    drawDamageNumber(g2, barX + BAR_WIDTH + 18, barY);
  }

  /**
   * Draws a pixel-art heart with a dark outline.
   */
  private void drawHeart(
      Graphics2D g2, int x, int y, int pixelSize) {

    String[] pattern = {
        "0110110",
        "1111111",
        "1111111",
        "0111110",
        "0011100",
        "0001000"
    };

    // Dark outline
    for (int row = 0; row < pattern.length; row++) {
      for (int col = 0; col < pattern[row].length(); col++) {
        if (pattern[row].charAt(col) == '1') {
          g2.setColor(BORDER);
          g2.fillRect(
              x + col * pixelSize - 1,
              y + row * pixelSize - 1,
              pixelSize + 2,
              pixelSize + 2);
        }
      }
    }

    // Red heart
    g2.setColor(RED);
    for (int row = 0; row < pattern.length; row++) {
      for (int col = 0; col < pattern[row].length(); col++) {
        if (pattern[row].charAt(col) == '1') {
          g2.fillRect(
              x + col * pixelSize,
              y + row * pixelSize,
              pixelSize,
              pixelSize);
        }
      }
    }
  }

  private void drawKeys(Graphics2D g2) {

    int x = HUD_X;
    int y = HUD_Y + 64;

    int keySize = 40;

    g2.drawImage(keyImage, x, y, keySize, keySize, null);

    g2.setFont(smallFont);
    g2.setColor(Color.WHITE);

    g2.drawString(
        "x " + gp.player.hasKey,
        x + 52,
        y + 28);
  }

  /**
   * Displays recent player damage near the health bar.
   */
  private void drawDamageNumber(
      Graphics2D g2, int x, int y) {

    long elapsed = System.currentTimeMillis()
        - gp.player.getLastDamageHudTime();

    long duration = gp.player.getDamageHudDuration();

    if (gp.player.getLastDamageHudTime() == 0 ||
        elapsed < 0 || elapsed >= duration) {
      return;
    }

    double progress = (double) elapsed / duration;

    int offsetY = (int) (progress * 16);
    float opacity = (float) (1.0 - progress);

    Composite oldComposite = g2.getComposite();

    g2.setComposite(AlphaComposite.getInstance(
        AlphaComposite.SRC_OVER, opacity));

    g2.setFont(smallFont);
    g2.setColor(new Color(255, 100, 100));

    g2.drawString(
        "-" + gp.player.getLastDamageAmount(),
        x,
        y + 18 - offsetY);

    g2.setComposite(oldComposite);
  }

  /**
   * Draws temporary messages.
   */
  private void drawMessage(Graphics2D g2) {

    if (!messageOn) {
      return;
    }

    g2.setFont(messageFont);
    g2.setColor(Color.WHITE);
    g2.drawString(message, 420, 300);

    messageCounter++;

    if (messageCounter > 2 * gp.FPS) {
      messageCounter = 0;
      messageOn = false;
    }
  }

  /**
   * Draws the victory screen.
   */
  private void drawVictory(Graphics2D g2) {

    g2.setFont(messageFont);
    g2.setColor(Color.WHITE);

    String text = "You Found the Treasure!";

    int x = (GamePanel.screenWidth -
        g2.getFontMetrics().stringWidth(text)) / 2;

    int y = GamePanel.screenHeight / 2;

    g2.drawString(text, x, y);
  }
}
