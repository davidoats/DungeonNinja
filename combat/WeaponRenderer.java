
package combat;

import items.Weapon;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.geom.Point2D;

/**
 * Draws weapon sprites independently of the player.
 *
 * Uses Java2D transformations to position and rotate
 * weapons without requiring separate animation sprites.
 */

public class WeaponRenderer {

  /**
   * Draws a weapon at a specified position and angle.
   *
   * @param g2     Graphics context
   * @param weapon Weapon to draw
   * @param x      Horizontal position of the weapon's handle
   * @param y      Vertical position of the weapon's handle
   * @param angle  Rotation in radians
   * @param width  Weapon sprite display width
   * @param height Weapon sprite display height
   */
  public void draw(Graphics2D g2, Weapon weapon, double x, double y, double angle, int width, int height) {

    if (weapon == null || weapon.getSprite() == null) {
      return;
    }

    BufferedImage sprite = weapon.getSprite();

    // Save the original graphics transformation
    AffineTransform original = g2.getTransform();

    try {
      // Move drawing origin to the weapon's handle
      g2.translate(x, y);

      // Rotate around the handle
      g2.rotate(angle);

      // Draw the weapon extending upward from its handle
      g2.drawImage(
          sprite,
          -width / 2,
          -height,
          width,
          height,
          null);

    } finally {
      // Restore transformation so other objects draw normally
      g2.setTransform(original);
    }
  }

  public Point2D.Double getBladeTip(
      Weapon weapon,
      double x, double y,
      double angle,
      int width, int height) {
    // Position of the tip relative to the handle pivot
    double localX = (weapon.getTipX() - 0.5) * width;
    double localY = (weapon.getTipY() - 1.0) * height;

    // Rotate the tip around the handle
    double rotatedX = localX * Math.cos(angle) - localY * Math.sin(angle);
    double rotatedY = localX * Math.sin(angle) + localY * Math.cos(angle);

    // Return the final position
    return new Point2D.Double(
        x + rotatedX,
        y + rotatedY);
  }

}
