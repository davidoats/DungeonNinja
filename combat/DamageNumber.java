
package combat;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * Displays a floating damage number above an enemy.
 */
public class DamageNumber {

  private final int damage;
  private final double worldX;
  private final double worldY;
  private final long startTime;

  private static final long DURATION = 700;

  public DamageNumber(int damage, double worldX, double worldY) {
    this.damage = damage;
    this.worldX = worldX;
    this.worldY = worldY;
    this.startTime = System.currentTimeMillis();
  }

  public boolean isExpired(long currentTime) {
    return currentTime - startTime >= DURATION;
  }

  public void draw(Graphics2D g2, double cameraX, double cameraY) {

    long elapsed = System.currentTimeMillis() - startTime;
    double progress = Math.min(1.0, (double) elapsed / DURATION);

    // Move upward over time
    double offsetY = progress * 35;

    int x = (int) (worldX - cameraX);
    int y = (int) (worldY - cameraY - offsetY);

    java.awt.Composite oldComposite = g2.getComposite();
    Font oldFont = g2.getFont();
    Color oldColor = g2.getColor();

    try {
      float opacity = (float) (1.0 - progress);

      g2.setComposite(java.awt.AlphaComposite.getInstance(
          java.awt.AlphaComposite.SRC_OVER, opacity));

      g2.setFont(new Font("Monospaced", Font.BOLD, 18));

      String text = String.valueOf(damage);
      int textWidth = g2.getFontMetrics().stringWidth(text);

      // Black outline/shadow
      g2.setColor(Color.BLACK);
      g2.drawString(text, x - textWidth / 2 + 1, y + 1);

      // White damage number
      g2.setColor(Color.WHITE);
      g2.drawString(text, x - textWidth / 2, y);

    } finally {
      g2.setComposite(oldComposite);
      g2.setFont(oldFont);
      g2.setColor(oldColor);
    }
  }
}
